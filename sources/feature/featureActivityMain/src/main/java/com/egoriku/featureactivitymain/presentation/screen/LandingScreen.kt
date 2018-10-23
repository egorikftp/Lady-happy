package com.egoriku.featureactivitymain.presentation.screen

import androidx.fragment.app.Fragment
import com.egoriku.core.actions.ILandingFragmentAction
import ru.terrakok.cicerone.android.support.SupportAppScreen

class LandingScreen(private val landingFragmentAction: ILandingFragmentAction) : SupportAppScreen() {

    override fun getFragment(): Fragment = landingFragmentAction.provideFragment()
}