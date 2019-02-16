package com.egoriku.mainscreen.presentation.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import com.egoriku.core.common.IMainActivityConnector
import com.egoriku.core.di.findDependencies
import com.egoriku.core.di.utils.INavigationHolder
import com.egoriku.ladyhappy.arch.activity.BaseInjectableActivity
import com.egoriku.ladyhappy.extensions.consume
import com.egoriku.ladyhappy.featureprovider.provider.FeatureScreen
import com.egoriku.mainscreen.R
import com.egoriku.mainscreen.common.findBehavior
import com.egoriku.mainscreen.di.MainActivityComponent
import kotlinx.android.synthetic.main.activity_main.*
import ru.semper_viventem.backdrop.BackdropBehavior
import ru.terrakok.cicerone.android.support.SupportAppNavigator
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

    private lateinit var backdropBehavior: BackdropBehavior

    private val navigator = object : SupportAppNavigator(this, R.id.foregroundContainer) {}

    override fun providePresenter() = mainActivityPresenter

    override fun provideLayout(): Int = R.layout.activity_main

    override fun injectDependencies() = MainActivityComponent.init(findDependencies()).inject(this)

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
        setSupportActionBar(toolbarMainActivity)

        if (savedInstanceState == null) {
            presenter.openLanding()
            navigationView.setCheckedItem(currentMenuItem)
            checkMenuPosition(navigationView.checkedItem!!.itemId)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return consume {
            menuInflater.inflate(R.menu.legal_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_main_setting -> consume {
                FeatureScreen.getSettingsFragment().apply {
                    show(supportFragmentManager, this.tag)
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ARGS_MENU_ITEM, navigationView.checkedItem!!.itemId)
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
}
