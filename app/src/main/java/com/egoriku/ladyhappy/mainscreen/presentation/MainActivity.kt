package com.egoriku.ladyhappy.mainscreen.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.egoriku.ladyhappy.R
import com.egoriku.ladyhappy.core.IFeatureProvider
import com.egoriku.ladyhappy.core.INavigationHolder
import com.egoriku.ladyhappy.core.constant.REQUEST_KEY_DYNAMIC_FEATURE
import com.egoriku.ladyhappy.core.constant.RESULT_KEY_DYNAMIC_FEATURE
import com.egoriku.ladyhappy.core.feature.*
import com.egoriku.ladyhappy.core.sharedmodel.toNightMode
import com.egoriku.ladyhappy.databinding.ActivityMainBinding
import com.egoriku.ladyhappy.extensions.*
import com.egoriku.ladyhappy.mainscreen.common.Constants.Tracking
import com.egoriku.ladyhappy.mainscreen.presentation.balloon.DynamicFeatureBalloonFactory
import com.egoriku.ladyhappy.mainscreen.presentation.components.dynamicFeature.DynamicFeatureEvent
import com.egoriku.ladyhappy.mainscreen.presentation.components.dynamicFeature.DynamicFeatureViewModel
import com.egoriku.ladyhappy.mainscreen.presentation.components.dynamicFeature.ModuleStatus
import com.egoriku.ladyhappy.mainscreen.presentation.components.inAppReview.ReviewViewModel
import com.egoriku.ladyhappy.mainscreen.presentation.components.inAppUpdates.InAppUpdateEvent
import com.egoriku.ladyhappy.mainscreen.presentation.components.inAppUpdates.InAppUpdateViewModel
import com.egoriku.ladyhappy.mainscreen.presentation.deeplink.AssistanceDeepLinkParser
import com.egoriku.ladyhappy.mainscreen.presentation.screen.*
import com.egoriku.ladyhappy.mainscreen.presentation.screen.params.DeepLinkScreen
import com.egoriku.ladyhappy.mainscreen.presentation.screen.params.ScreenParams
import com.egoriku.ladyhappy.navigation.navigator.platform.ActivityScopeNavigator
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.balloon
import kotlinx.coroutines.flow.collect
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

private const val INSTALL_CONFIRMATION_REQ_CODE = 1
private const val UPDATE_CONFIRMATION_REQ_CODE = 2
private const val KEY_SELECTED_MENU_ITEM = "selected_item"

class MainActivity : ScopeActivity(R.layout.activity_main) {

    private val dynamicFeatureBalloon by balloon<DynamicFeatureBalloonFactory>()

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.contentFullScreen)

    private val appUpdateManager: AppUpdateManager by inject()
    private val splitInstallManager: SplitInstallManager by inject()
    private val featureProvider: IFeatureProvider by inject()
    private val navigatorHolder: INavigationHolder by inject()

    private val dynamicFeatureViewModel by viewModel<DynamicFeatureViewModel>()
    private val inAppUpdateViewModel by viewModel<InAppUpdateViewModel>()
    private val reviewViewModel by viewModel<ReviewViewModel>()
    private val viewModel by viewModel<MainActivityViewModel>(state = { bundleOf() })

    private val navigator = ActivityScopeNavigator(
            activity = this,
            containerId = R.id.container,
            fullScreenContainerId = R.id.contentFullScreen
    )

    private var snackBar: Snackbar by Delegates.notNull()

    private val Balloon.balloonTitleTextView: TextView
        get() = getContentView().findViewById(R.id.title)

    private val Balloon.balloonProgressBar: ProgressBar
        get() = getContentView().findViewById(R.id.progressBar)

    private var isOpenDynamicFeatureWhenReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!hasM()) {
            window.statusBarColor = Color.BLACK
        }

        setSupportActionBar(binding.toolbarMainActivity)

        snackBar = Snackbar.make(
                binding.bottomNavigation,
                R.string.in_app_update_available,
                Snackbar.LENGTH_INDEFINITE
        ).apply {
            anchorView = binding.bottomNavigation
        }

        viewModel.screenTitle.observe(this) {
            binding.toolbarContent.headerBarLogoText.setText(it)
        }

        when (savedInstanceState) {
            null -> viewModel.replaceWith(
                    screen = CatalogScreen(featureProvider),
                    params = ScreenParams(
                            screenNameResId = R.string.navigation_view_catalog_header,
                            trackingScreenName = Tracking.TRACKING_FRAGMENT_CATALOG
                    )
            )
            else -> {
                with(savedInstanceState.getInt(KEY_SELECTED_MENU_ITEM)) {
                    binding.bottomNavigation.selectedItemId = this
                }
            }
        }

        with(binding.bottomNavigation) {
            setOnNavigationItemSelectedListener { item ->
                consume {
                    mapItemIdToScreen(item.itemId)
                }
            }

            setOnNavigationItemReselectedListener {}
        }

        viewModel.theme.observe(this) {
            AppCompatDelegate.setDefaultNightMode(it.toNightMode())
        }

        reviewViewModel.submitReview { reviewInfo, reviewManager ->
            reviewManager.launchReviewFlow(this, reviewInfo)
        }

        expandAppBarLayoutInPage()

        subscribeForInAppUpdate()
        subscribeForDynamicFeatureInstall()

        subscribeForDynamicFeatureRequest()

        intent?.handleGoogleAssistanceSearchIntent()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.handleGoogleAssistanceSearchIntent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            UPDATE_CONFIRMATION_REQ_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> viewModel.trackInAppUpdateSuccess()
                    Activity.RESULT_CANCELED -> viewModel.trackInAppUpdateCanceled()
                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> viewModel.trackInAppUpdateFailed()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() = viewModel.onBackPressed()

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_MENU_ITEM, binding.bottomNavigation.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }

    private fun subscribeForInAppUpdate() {
        lifecycleScope.launchWhenResumed {
            inAppUpdateViewModel.updateStatus.collect { updateResult: AppUpdateResult ->
                updateUpdateButton(updateResult)

                // If it's an immediate update, launch it immediately and finish Activity
                // to prevent the user from using the app until they update.
                if (updateResult is AppUpdateResult.Available) {
                    if (inAppUpdateViewModel.shouldLaunchImmediateUpdate(updateResult.updateInfo)) {
                        if (appUpdateManager.startUpdateFlowForResult(
                                        updateResult.updateInfo,
                                        AppUpdateType.IMMEDIATE,
                                        this@MainActivity,
                                        UPDATE_CONFIRMATION_REQ_CODE)) {
                            finish()
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            inAppUpdateViewModel.events.collect { event ->
                when (event) {
                    is InAppUpdateEvent.ToastEvent -> toast(event.message)
                    is InAppUpdateEvent.StartUpdateEvent -> {
                        val updateType = when {
                            event.immediate -> AppUpdateType.IMMEDIATE
                            else -> AppUpdateType.FLEXIBLE
                        }
                        appUpdateManager.startUpdateFlowForResult(
                                event.updateInfo,
                                updateType,
                                this@MainActivity,
                                UPDATE_CONFIRMATION_REQ_CODE
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun subscribeForDynamicFeatureInstall() {
        lifecycleScope.launchWhenResumed {
            dynamicFeatureViewModel.events.collect { event ->
                when (event) {
                    is DynamicFeatureEvent.ToastEvent -> toast(event.message)
                    is DynamicFeatureEvent.NavigationEvent -> {
                        viewModel.navigateTo(screen = DynamicFeatureScreen(event.fragmentClass))
                    }
                    is DynamicFeatureEvent.InstallConfirmationEvent -> {
                        splitInstallManager.startConfirmationDialogForResult(
                                event.status,
                                this@MainActivity,
                                INSTALL_CONFIRMATION_REQ_CODE
                        )
                    }
                    else -> throw IllegalStateException("Event type not handled: $event")
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            dynamicFeatureViewModel.postCreatorModuleStatus.collect { status ->
                when (status) {
                    is ModuleStatus.Installing -> {
                        with(dynamicFeatureBalloon) {
                            showAlignTop(binding.bottomNavigation)

                            val progress = (status.progress * 100).toInt()

                            balloonTitleTextView.text = getString(R.string.dynamic_delivery_installing, progress)
                            balloonProgressBar.apply {
                                isIndeterminate = false
                                setProgress(progress)
                            }
                        }
                    }
                    is ModuleStatus.Unavailable -> {
                        with(dynamicFeatureBalloon) {
                            balloonTitleTextView.setText(R.string.dynamic_delivery_feature_not_available)
                            balloonProgressBar.isIndeterminate = false
                        }

                        dynamicFeatureBalloon.dismissWithDelay(1.seconds.toLongMilliseconds())
                    }
                    is ModuleStatus.Installed -> {
                        if (isOpenDynamicFeatureWhenReady) {
                            isOpenDynamicFeatureWhenReady = false
                            dynamicFeatureViewModel.invokePostCreator()
                        }

                        dynamicFeatureBalloon.dismissWithDelay(1.seconds.toLongMilliseconds())
                    }
                    is ModuleStatus.NeedsConfirmation -> {
                        splitInstallManager.startConfirmationDialogForResult(
                                status.state,
                                this@MainActivity,
                                UPDATE_CONFIRMATION_REQ_CODE
                        )
                    }
                    ModuleStatus.None -> {
                    }
                }
            }
        }
    }

    private fun subscribeForDynamicFeatureRequest() {
        supportFragmentManager.setFragmentResultListenerWrapper(
                requestKey = REQUEST_KEY_DYNAMIC_FEATURE,
                lifecycleOwner = this,
                listener = { _, bundle ->
                    when (bundle.getParcelable<DynamicFeature>(RESULT_KEY_DYNAMIC_FEATURE)) {
                        is DynamicFeature.PostCreator -> {
                            isOpenDynamicFeatureWhenReady = true
                            dynamicFeatureViewModel.invokePostCreator()
                        }
                    }
                }
        )
    }

    private fun Intent.handleGoogleAssistanceSearchIntent() {
        AssistanceDeepLinkParser().process(intent = this) { featureScreen ->
            when (featureScreen) {
                is DeepLinkScreen.Search -> {
                    viewModel.replaceWith(
                            screen = SearchScreen(featureProvider, featureScreen.searchQuery),
                            params = ScreenParams(
                                    screenNameResId = R.string.navigation_view_search_header,
                                    trackingScreenName = Tracking.TRACKING_FRAGMENT_SEARCH
                            )
                    )
                }
                is DeepLinkScreen.Catalog -> preselectAndNavigate(R.id.menuCatalog)
                is DeepLinkScreen.About -> preselectAndNavigate(R.id.menuLanding)
                is DeepLinkScreen.News -> preselectAndNavigate(R.id.menuPhotoReport)
                is DeepLinkScreen.Settings -> preselectAndNavigate(R.id.menuSettings)
                is DeepLinkScreen.Unknown -> toast(getString(R.string.deeplink_unknown_screen))
            }
        }
    }

    private fun preselectAndNavigate(@IdRes menuItemId: Int) {
        binding.bottomNavigation.selectedItemId = menuItemId
        mapItemIdToScreen(menuItemId)
    }

    private fun mapItemIdToScreen(@IdRes menuItemId: Int) {
        when (menuItemId) {
            R.id.menuLanding -> viewModel.replaceWith(
                    screen = LandingScreen(featureProvider),
                    params = ScreenParams(
                            screenNameResId = R.string.navigation_view_landing_header,
                            trackingScreenName = Tracking.TRACKING_FRAGMENT_LANDING
                    )
            )
            R.id.menuPhotoReport -> viewModel.replaceWith(
                    screen = PhotoReportScreen(featureProvider),
                    params = ScreenParams(
                            screenNameResId = R.string.navigation_view_photo_report_header,
                            trackingScreenName = Tracking.TRACKING_FRAGMENT_PHOTO_REPORT
                    )
            )
            R.id.menuCatalog -> viewModel.replaceWith(
                    screen = CatalogScreen(featureProvider),
                    params = ScreenParams(
                            screenNameResId = R.string.navigation_view_catalog_header,
                            trackingScreenName = Tracking.TRACKING_FRAGMENT_CATALOG
                    )
            )
            R.id.menuSettings -> viewModel.replaceWith(
                    screen = SettingsScreen(featureProvider),
                    params = ScreenParams(
                            screenNameResId = R.string.navigation_view_settings_header,
                            trackingScreenName = Tracking.TRACKING_FRAGMENT_SETTINGS
                    )
            )
        }
    }

    private fun expandAppBarLayoutInPage() {
        supportFragmentManager.addFragmentOnAttachListener { _, fragment ->
            when (fragment) {
                is CatalogFeature, is AboutUsFeature, is PhotoReportsFeature, is SettingsFeature -> {
                    binding.appBarLayout.setExpanded(true)
                }
            }
        }
    }

    private fun updateUpdateButton(updateResult: AppUpdateResult) {
        when (updateResult) {
            AppUpdateResult.NotAvailable -> {
                logD("No update available")
                snackBar.dismiss()
            }
            is AppUpdateResult.Available -> with(snackBar) {
                setText(R.string.in_app_update_available)
                setAction(R.string.in_app_update_now) {
                    inAppUpdateViewModel.invokeUpdate()
                }
                show()
            }
            is AppUpdateResult.InProgress -> {
                with(snackBar) {
                    val updateProgress: Int = calculateUpdateProgress(updateResult)

                    setText(getString(R.string.in_app_update_downloading, updateProgress))
                    setAction(null) {}
                    show()
                }
            }
            is AppUpdateResult.Downloaded -> {
                with(snackBar) {
                    setText(R.string.in_app_update_download_finished)
                    setAction(R.string.in_app_update_complete) {
                        inAppUpdateViewModel.invokeUpdate()
                    }
                    show()
                }
            }
        }
    }

    private fun calculateUpdateProgress(updateResult: AppUpdateResult.InProgress): Int =
            when (updateResult.installState.totalBytesToDownload) {
                0L -> 0
                else -> {
                    (updateResult.installState.bytesDownloaded * 100 /
                            updateResult.installState.totalBytesToDownload).toInt()
                }
            }
}