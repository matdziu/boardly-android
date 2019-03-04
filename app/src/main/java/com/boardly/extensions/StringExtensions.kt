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

fun String.noSpecialChars(): String {
    return replace("ą", "a")
            .replace("ę", "e")
            .replace("ć", "c")
            .replace("ł", "l")
            .replace("ń", "n")
            .replace("ó", "o")
            .replace("ś", "s")
            .replace("ź", "z")
            .replace("ż", "z")
}