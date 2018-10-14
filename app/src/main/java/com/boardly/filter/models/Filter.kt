package com.boardly.filter.models

import android.os.Parcelable
import com.boardly.common.location.UserLocation
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Filter(var radius: Double = 50.0,
                  var gameId: String = "",
                  var gameName: String = "",
                  var userLocation: UserLocation? = null,
                  var locationName: String = "",
                  var isCurrentLocation: Boolean = true) : Parcelable