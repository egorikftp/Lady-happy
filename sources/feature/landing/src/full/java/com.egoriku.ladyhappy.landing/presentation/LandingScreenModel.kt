package com.egoriku.ladyhappy.landing.presentation

import com.egoriku.ladyhappy.landing.domain.model.LandingModel

class LandingScreenModel(
        val loadState: LoadState = LoadState.NONE,
        val landingModel: LandingModel? = null
) {

    fun isEmpty() = landingModel == null
}

//TODO use sealed classes
enum class LoadState {
    NONE,
    PROGRESS,
    ERROR_LOADING,
}