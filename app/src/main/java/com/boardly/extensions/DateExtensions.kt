package com.boardly.extensions

import java.text.SimpleDateFormat
import java.util.*

const val HOUR_IN_MILLIS = 1 * 60 * 60 * 1000

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

fun isOlderThanOneHour(timestamp: Long): Boolean {
    val now = System.currentTimeMillis()
    return (now - timestamp) > HOUR_IN_MILLIS && timestamp != 0L
}