package com.boardly.common.location

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLocation(val latitude: Double, val longitude: Double) : Parcelable