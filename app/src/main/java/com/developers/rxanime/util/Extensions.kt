package com.developers.rxanime.util

import android.content.res.Resources

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

inline fun <reified T : Enum<T>> String.getOperator(): T? {
    return enumValues<T>().firstOrNull { it.name == this }
}
