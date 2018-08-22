package com.boardly.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.formatForDisplay(): String {
    val simpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.getDefault())
    return simpleDateFormat.format(this)
}

fun getCurrentISODate(): String {
    val timeZone = TimeZone.getTimeZone("UTC")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US)
    dateFormat.timeZone = timeZone
    return dateFormat.format(Date())
}