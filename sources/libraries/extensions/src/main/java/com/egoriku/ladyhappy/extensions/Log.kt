package com.egoriku.ladyhappy.extensions

import android.util.Log
import com.egoriku.ladyhappy.extensions.common.Constants.EMPTY

inline fun <reified T> T.logD(message: String? = EMPTY) {
    when {
        message.isNullOrEmpty() -> Log.d(T::class.java.simpleName, EMPTY)
        else -> Log.d(T::class.java.simpleName, message)
    }
}