package com.boardly.filter.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Filter(var radius: Double = 1.0, var gameId: String = "") : Parcelable