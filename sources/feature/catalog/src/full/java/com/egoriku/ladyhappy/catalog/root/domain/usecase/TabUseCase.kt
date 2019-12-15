package com.egoriku.ladyhappy.catalog.root.domain.usecase

import com.egoriku.ladyhappy.catalog.root.data.entity.TabEntity
import com.egoriku.ladyhappy.catalog.root.data.repository.TabRepository
import com.egoriku.ladyhappy.catalog.root.domain.model.TabItem
import com.egoriku.ladyhappy.catalog.root.presentation.RootScreenModel
import com.egoriku.network.Result.Error
import com.egoriku.network.Result.Success

class TabUseCase(private val tabRepository: TabRepository) {

    suspend fun loadTabs(): RootScreenModel = when (val result = tabRepository.load()) {
        is Success -> {
            val list = result.value

            when {
                list.isEmpty() -> RootScreenModel.Error()
                else -> RootScreenModel.Success(list.mapNotNull { it.mapOrNull() })
            }
        }
        is Error -> RootScreenModel.Error()
    }

    private fun TabEntity.mapOrNull() = if (id != null && name != null && documentId != null) {
        TabItem(
                id = id,
                name = name,
                documentId = documentId
        )
    } else null
}