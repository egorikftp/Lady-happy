package com.egoriku.mainscreen.presentation.screen

import android.os.Bundle
import androidx.core.os.bundleOf
import com.egoriku.ladyhappy.featureprovider.provider.FeatureScreen
import com.egoriku.ladyhappy.navigation.screen.FragmentScreen
import com.egoriku.mainscreen.R
import com.egoriku.mainscreen.common.Constants.Tracking
import com.egoriku.mainscreen.common.TITLE_KEY
import com.egoriku.mainscreen.common.TRACKING_KEY

class LandingScreen : FragmentScreen() {

    override val arguments: Bundle = bundleOf(
            TITLE_KEY to R.string.navigation_view_landing_header,
            TRACKING_KEY to Tracking.TRACKING_FRAGMENT_LANDING
    )

    override val fragment = FeatureScreen.getLandingFragment()
}
