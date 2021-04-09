package com.egoriku.ladyhappy.network

sealed class ResultOf<out T> {
    class Success<out T>(val value: T) : ResultOf<T>()
    class Failure(val throwable: Throwable) : ResultOf<Nothing>()
}

inline fun <reified T> ResultOf<T>.doIfFailure(callback: (throwable: Throwable?) -> Unit) {
    if (this is ResultOf.Failure) {
        callback(throwable)
    }
}

inline fun <reified T> ResultOf<T>.doIfSuccess(callback: (value: T) -> Unit) {
    if (this is ResultOf.Success) {
        callback(value)
    }
}

fun <T> ResultOf<T>.successOr(fallback: T): T = (this as? ResultOf.Success<T>)?.value ?: fallback