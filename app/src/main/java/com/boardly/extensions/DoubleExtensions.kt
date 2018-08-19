package com.boardly.extensions

import java.text.DecimalFormat

fun Double.toString(pattern: String): String {
    return DecimalFormat(pattern).format(this)
}