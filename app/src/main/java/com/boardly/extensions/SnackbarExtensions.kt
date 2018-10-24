package com.boardly.extensions

import android.support.annotation.ColorRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.widget.TextView

fun Snackbar.setTextColor(@ColorRes color: Int): Snackbar {
    val tv = view.findViewById(android.support.design.R.id.snackbar_text) as TextView
    tv.setTextColor(ContextCompat.getColor(context, color))
    return this
}

fun Snackbar.setMaxLines(maxLines: Int): Snackbar {
    val tv = view.findViewById(android.support.design.R.id.snackbar_text) as TextView
    tv.maxLines = maxLines
    return this
}


fun Snackbar.setBackgroundColor(@ColorRes color: Int): Snackbar {
    view.setBackgroundColor(ContextCompat.getColor(context, color))
    return this
}

fun Snackbar.simplySetActionTextColor(@ColorRes color: Int): Snackbar {
    setActionTextColor(ContextCompat.getColor(context, color))
    return this
}