package com.egoriku.ladyhappy.catalog.categories.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.egoriku.ladyhappy.catalog.categories.domain.usecase.ITabUseCase

class RootCatalogViewModel(private val tabUseCase: ITabUseCase) : ViewModel() {

    val screenModel: LiveData<RootScreenModel> = liveData {
        emit(RootScreenModel.Progress)

        emit(tabUseCase.loadTabs())
    }
}