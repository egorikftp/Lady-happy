package com.egoriku.landing.presentation

import android.util.Log
import com.egoriku.core.di.utils.IAnalytics
import com.egoriku.core.exception.FirestoreNetworkException
import com.egoriku.core.exception.FirestoreParseException
import com.egoriku.core.exception.NoSuchDocumentException
import com.egoriku.network.Result
import com.egoriku.landing.domain.interactors.LandingUseCase
import com.egoriku.landing.domain.model.LandingModel
import com.egoriku.ladyhappy.arch.pvm.BasePresenter
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class LandingPagePresenter
@Inject constructor(private val analytics: IAnalytics,
                    private val landingUseCase: LandingUseCase)
    : BasePresenter<LandingPageContract.View>(), LandingPageContract.Presenter, CoroutineScope {

    private var screenModel: LandingScreenModel = LandingScreenModel()
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun loadLandingData() {
        when {
            !screenModel.isEmpty() -> view?.render(screenModel)
            else -> getLandingData()
        }
    }

    private fun getLandingData() {
        launch {
            processResult(LoadState.PROGRESS)

            val result: Result<LandingModel> = withContext(Dispatchers.IO) {
                landingUseCase.getLandingInfo()
            }

            when (result) {
                is Result.Success -> processResult(LoadState.NONE, result.value)

                is Result.Error -> {
                    when (result.exception) {
                        is FirestoreNetworkException -> {
                            Log.e("LandingPagePresenter", "FirestoreNetworkException", result.exception)
                            analytics.trackNoInternetLanding()
                        }
                        is FirestoreParseException -> Log.e("LandingPagePresenter", "FirestoreParseException", result.exception)
                        is NoSuchDocumentException -> Log.e("LandingPagePresenter", "NoSuchDocumentException", result.exception)
                    }

                    processResult(LoadState.ERROR_LOADING)
                }
            }
        }
    }

    override fun detachView() {
        job.cancel()
        super.detachView()
    }

    override fun retryLoading() = getLandingData()

    private fun processResult(loadState: LoadState = LoadState.NONE, model: LandingModel? = null) {
        screenModel.let {
            it.landingModel = model
            it.loadState = loadState

            view?.render(it)
        }
    }
}