package com.egoriku.ladyhappy.catalog.categories.koin

import com.egoriku.ladyhappy.catalog.categories.data.repository.ITabRepository
import com.egoriku.ladyhappy.catalog.categories.data.repository.TabRepository
import com.egoriku.ladyhappy.catalog.categories.domain.usecase.ITabUseCase
import com.egoriku.ladyhappy.catalog.categories.domain.usecase.TabUseCase
import com.egoriku.ladyhappy.catalog.categories.presentation.RootCatalogViewModel
import com.egoriku.ladyhappy.catalog.categories.presentation.fragment.CategoriesFragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val categoriesModule = module {
    scope<CategoriesFragment> {
        scoped<ITabRepository> { TabRepository(firebase = get()) }

        scoped<ITabUseCase> { TabUseCase(tabRepository = get()) }

        viewModel {
            RootCatalogViewModel(tabUseCase = get())
        }
    }
}