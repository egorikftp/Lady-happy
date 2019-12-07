package com.egoriku.ladyhappy.catalog.category.presentation.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.egoriku.ladyhappy.catalog.R
import com.egoriku.ladyhappy.catalog.category.presentation.CatalogListViewModel
import com.egoriku.ladyhappy.catalog.category.presentation.CatalogScreenState
import com.egoriku.ladyhappy.catalog.category.presentation.CatalogScreenState.Success
import com.egoriku.ladyhappy.catalog.category.presentation.adapter.controller.CatalogController
import com.egoriku.ladyhappy.catalog.category.presentation.adapter.decorator.MarginItemDecoration
import kotlinx.android.synthetic.main.fragment_catalog.*
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.surfstudio.android.easyadapter.ItemList
import kotlin.properties.Delegates

class CatalogFragment : Fragment(R.layout.fragment_catalog) {

    private val viewModelScope = getKoin().getOrCreateScope("CatalogFragment", named<CatalogFragment>())

    private val catalogViewModel: CatalogListViewModel = viewModelScope.get()

    private var catalogController: CatalogController by Delegates.notNull()

    private val catalogAdapter = EasyAdapter().apply {
        setFirstInvisibleItemEnabled(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        catalogController = CatalogController {
            Toast.makeText(context, "Item ${it.itemName} was clicked", LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        catalogRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = catalogAdapter
            addItemDecoration(MarginItemDecoration(context.resources.getDimension(R.dimen.adapter_item_catalog_margin).toInt()))
        }

        catalogViewModel.catalogItems.observe(this) {
            render(it)
        }
    }

    private fun render(screenState: CatalogScreenState) {
        when (screenState) {
            is Success -> {
                catalogAdapter.setItems(
                        ItemList.create()
                                .addAll(screenState.screenData, catalogController)
                )
            }

            is Error -> {
                //TODO handle error state
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelScope.close()
    }
}