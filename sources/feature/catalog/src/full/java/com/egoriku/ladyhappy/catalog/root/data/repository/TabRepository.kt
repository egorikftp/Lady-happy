package com.egoriku.ladyhappy.catalog.root.data.repository

import com.egoriku.core.di.utils.IFirebaseFirestore
import com.egoriku.ladyhappy.catalog.root.data.entity.TabEntity
import com.egoriku.network.Result
import com.egoriku.network.firestore.awaitResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TabRepository(private val firebase: IFirebaseFirestore) {

    suspend fun load(): Result<List<TabEntity>> = withContext(Dispatchers.IO) {
        firebase.getFirebase()
                .collection("categories")
                .awaitResult<TabEntity>()
    }
}