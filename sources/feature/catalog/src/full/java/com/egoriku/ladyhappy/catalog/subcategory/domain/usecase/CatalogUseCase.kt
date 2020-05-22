package com.egoriku.ladyhappy.catalog.subcategory.domain.usecase

import com.egoriku.ladyhappy.catalog.subcategory.data.entity.Image
import com.egoriku.ladyhappy.catalog.subcategory.data.entity.SubCategoryEntity
import com.egoriku.ladyhappy.catalog.subcategory.data.repository.SubcategoryRepository
import com.egoriku.ladyhappy.catalog.subcategory.domain.model.SubCategoryItem
import com.egoriku.mozaik.model.MozaikImageItem
import com.egoriku.mozaik.model.PhotoSizes
import com.egoriku.network.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CatalogUseCase(private val subcategoryRepository: SubcategoryRepository) {

    private val entityTransform: (SubCategoryEntity) -> SubCategoryItem = { entity: SubCategoryEntity ->
        SubCategoryItem(
                images = entity.images.map(imageTransform),
                name = entity.categoryName,
                isPopular = entity.isPopular,
                publishedCount = entity.publishedCount
        )
    }

    private val imageTransform: (Image) -> MozaikImageItem = { image: Image ->
        MozaikImageItem(
                width = image.width,
                height = image.height,
                sizes = PhotoSizes(original = image.url)
        )
    }

    suspend fun loadSubCategories(categoryId: Int): Result<List<SubCategoryItem>> =
            when (val subcategories = subcategoryRepository.fetchSubCategories(categoryId)) {
                is Result.Error -> Result.Error(Exception("Response empty"))
                is Result.Success -> Result.Success(
                        withContext(Dispatchers.Default) {
                            subcategories.value
                                    .map(entityTransform)
                        }
                )
            }
}