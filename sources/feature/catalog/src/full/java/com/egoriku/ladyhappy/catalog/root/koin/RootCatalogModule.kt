package com.egoriku.ladyhappy.catalog.root.koin

import com.egoriku.ladyhappy.catalog.root.data.repository.TabRepository
import com.egoriku.ladyhappy.catalog.root.domain.usecase.TabUseCase
import com.egoriku.ladyhappy.catalog.root.presentation.RootCatalogViewModel
import com.egoriku.ladyhappy.catalog.root.presentation.fragment.RootCatalogFragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.core.scope.ScopeID
import org.koin.dsl.module

object RootCatalogModule {
    val module = module {
        scope(named<RootCatalogFragment>()) {
            scoped { TabUseCase(tabRepository = get()) }
            scoped { TabRepository(firebase = get()) }
        }

        viewModel { (scopeId: ScopeID) ->
            RootCatalogViewModel(tabUseCase = getScope(scopeId).get())
        }
    }
}