package com.egoriku.featureactivitymain.presentation.activity

import android.content.Context
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import com.egoriku.core.actions.ILandingFragmentAction
import com.egoriku.core.actions.IPhotoReportFragmentAction
import com.egoriku.core.actions.common.IMainActivityConnector
import com.egoriku.core.di.findDependencies
import com.egoriku.core.di.utils.INavigationHolder
import com.egoriku.featureactivitymain.R
import com.egoriku.featureactivitymain.common.findBehavior
import com.egoriku.featureactivitymain.di.MainActivityComponent
import com.egoriku.featureactivitymain.presentation.activity.MainActivityPresenter.Companion.LANDING_PAGE
import com.egoriku.featureactivitymain.presentation.activity.MainActivityPresenter.Companion.PHOTO_REPORT
import com.egoriku.ui.arch.activity.BaseInjectableActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.semper_viventem.backdrop.BackdropBehavior
import ru.terrakok.cicerone.android.SupportAppNavigator
import javax.inject.Inject

class MainActivity : BaseInjectableActivity<MainActivityContract.View, MainActivityContract.Presenter>(), MainActivityContract.View,
        IMainActivityConnector {

    companion object {
        private const val ARGS_MENU_ITEM = "selected_item"

        private val MENU_LANDING = R.id.menuLanding
        private val MENU_PHOTO_REPORT = R.id.menuPhotoReport

        private val DEFAULT_MENU_ITEM = MENU_LANDING
    }

    @Inject
    lateinit var mainActivityPresenter: MainActivityContract.Presenter

    @Inject
    lateinit var navigatorHolder: INavigationHolder

    @Inject
    lateinit var landingFragmentAction: ILandingFragmentAction

    @Inject
    lateinit var photoReportFragmentAction: IPhotoReportFragmentAction

    private lateinit var backdropBehavior: BackdropBehavior

    private val navigator = object : SupportAppNavigator(this, supportFragmentManager, R.id.foregroundContainer) {

        override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?) = null

        override fun createFragment(screenKey: String?, data: Any?): Fragment? = when (screenKey) {
            LANDING_PAGE -> landingFragmentAction.provideFragment()
            PHOTO_REPORT -> photoReportFragmentAction.provideFragment()
            else -> null
        }
    }

    override fun initPresenter() = mainActivityPresenter

    override fun provideLayout(): Int = R.layout.activity_main

    override fun injectDependencies() {
        MainActivityComponent.Initializer.init(findDependencies())
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backdropBehavior = foregroundContainer.findBehavior()

        with(backdropBehavior) {
            attachBackContainer(R.id.backContainer)
            attachToolbar(R.id.toolbarMainActivity)
            setClosedIcon(R.drawable.ic_menu)
            setOpenedIcon(R.drawable.ic_close)
        }

        with(navigationView) {
            setNavigationItemSelectedListener { item ->
                checkMenuPosition(item.itemId)
                backdropBehavior.close()
                true
            }
        }

        val currentMenuItem = savedInstanceState?.getInt(ARGS_MENU_ITEM) ?: DEFAULT_MENU_ITEM
        navigationView.setCheckedItem(currentMenuItem)
        checkMenuPosition(navigationView.checkedItem!!.itemId)

        setSupportActionBar(toolbarMainActivity)

        if (savedInstanceState == null) {
            presenter.openLanding()
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

    override fun onBackPressed() {
        if (backdropBehavior.close()) {
            return
        } else if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
            return
        }

        presenter.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(ARGS_MENU_ITEM, navigationView.checkedItem!!.itemId)
        super.onSaveInstanceState(outState)
    }

    private fun checkMenuPosition(@IdRes menuItemId: Int) {
        when (menuItemId) {
            MENU_LANDING -> presenter.openLanding()
            MENU_PHOTO_REPORT -> presenter.openPhotoReport()
        }
    }

    override fun onScroll() {
        backdropBehavior.close()
    }

    @Deprecated("move to fragment view contract")
    override fun showLoading() {
    }

    @Deprecated("move to fragment view contract")
    override fun hideLoading() {
    }
}
