package com.boardly.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.formatForDisplay(): String {
    val simpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.getDefault())
    return simpleDateFormat.format(this)
}