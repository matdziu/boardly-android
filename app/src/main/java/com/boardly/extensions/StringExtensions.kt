package com.boardly.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun String?.jsonToArrayOfStrings(): Array<String> {
    val gson = Gson()
    val arrayType = object : TypeToken<Array<String>>() {}.type
    return gson.fromJson(this, arrayType)
}

fun String?.jsonToMapOfStrings(): Map<String, String> {
    val gson = Gson()
    val mapType = object : TypeToken<Map<String, String>>() {}.type
    return gson.fromJson(this, mapType)
}