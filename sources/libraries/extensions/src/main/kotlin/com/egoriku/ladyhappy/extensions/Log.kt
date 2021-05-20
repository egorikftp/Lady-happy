package com.egoriku.ladyhappy.extensions

import android.util.Log
import com.egoriku.ladyhappy.extensions.common.Constants.EMPTY

inline fun <reified T> T.logD(message: String? = EMPTY) {
    when {
        message.isNullOrEmpty() -> Log.d(T::class.java.simpleName, EMPTY)
        else -> Log.d(T::class.java.simpleName, message)
    }
}

inline fun <reified T> T.logD(tag: String, message: String? = EMPTY) {
    when {
        message.isNullOrEmpty() -> Log.d(tag, EMPTY)
        else -> Log.d(tag, message)
    }
}

inline fun <reified T> T.logE(message: String? = null, throwable: Throwable? = null) {
    Log.e(T::class.java.simpleName, message, throwable)
}

inline fun <reified T> T.logE(
    tag: String? = null,
    message: String? = null,
    throwable: Throwable? = null
) {
    if (tag.isNullOrEmpty()) {
        Log.e(T::class.java.simpleName, message, throwable)
    } else {
        Log.e(tag, message, throwable)
    }
}

inline fun <reified T> T.logDm(message: String? = EMPTY) {
    when {
        message.isNullOrEmpty() -> Log.d("egorikftp", EMPTY)
        else -> Log.d("egorikftp", message)
    }
}