package com.boardly.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun String?.jsonToArrayOfStrings(): Array<String> {
    return if (this != null) {
        val gson = Gson()
        val arrayType = object : TypeToken<Array<String>>() {}.type
        gson.fromJson(this, arrayType)
    } else {
        emptyArray()
    }
}

fun String?.jsonToMapOfStrings(): Map<String, String> {
    return if (this != null) {
        val gson = Gson()
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        gson.fromJson(this, mapType)
    } else {
        emptyMap()
    }
}

fun String.isOfType(type: String): Boolean = contains(type)

fun String.clearFromType(type: String): String = replace(type, "")