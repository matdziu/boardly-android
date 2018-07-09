package com.boardly.extensions

fun String.formatForMaxOf(max: String): String {
    return "${this}/$max"
}