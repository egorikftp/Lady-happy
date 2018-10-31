package com.egoriku.network.data.entities.landing

import com.google.firebase.firestore.PropertyName

class QuotesEntity {
    @PropertyName("quote")
    @JvmField
    val quote: String? = null

    @PropertyName("author")
    @JvmField
    val author: String? = null
}