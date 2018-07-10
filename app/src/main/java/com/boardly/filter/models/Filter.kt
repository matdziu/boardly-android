package com.boardly.filter.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Filter(val radius: Double, val gameId: String = "") : Parcelable