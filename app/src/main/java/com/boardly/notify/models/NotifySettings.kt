package com.boardly.notify.models

import com.boardly.common.location.UserLocation

data class NotifySettings(var radius: Double = 50.0,
                          var gameId: String = "",
                          var userLocation: UserLocation? = null)