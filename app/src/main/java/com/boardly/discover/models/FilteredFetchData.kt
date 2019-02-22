package com.boardly.discover.models

import com.boardly.common.location.UserLocation

data class FilteredFetchData(val userLocation: UserLocation,
                             val radius: Double = 50.0)