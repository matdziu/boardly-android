package com.boardly.extensions

import android.content.SharedPreferences

fun SharedPreferences.readAppSetting(settingKey: String) = getBoolean(settingKey, true)

fun SharedPreferences.saveAppSetting(settingKey: String, value: Boolean) = edit().putBoolean(settingKey, value).apply()