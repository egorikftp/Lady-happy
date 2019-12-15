@file:Suppress("NOTHING_TO_INLINE")

package com.egoriku.ladyhappy.extensions

import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View

inline fun View.gone() {
    if (visibility != View.GONE) visibility = View.GONE
}

inline fun View.invisible() {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}

inline fun View.visible() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

fun View.withStyledAttributes(
        attributeSet: AttributeSet? = null,
        styleArray: IntArray,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0,
        block: TypedArray.() -> Unit
) = context.withStyledAttributes(
        set = attributeSet,
        attrs = styleArray,
        defStyleAttr = defStyleAttr,
        defStyleRes = defStyleRes,
        block = block
)