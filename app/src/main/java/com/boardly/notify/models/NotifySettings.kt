package com.boardly.notify.models

import com.boardly.common.location.UserLocation

data class NotifySettings(var radius: Double = 50.0,
                          var gameId: String = "",
                          var gameName: String = "",
                          var userLocation: UserLocation? = null,
                          var locationName: String = "") {

    fun toMap(): Map<String, Any> {
        return mapOf(
                "radius" to radius,
                "gameId" to gameId,
                "gameName" to gameName,
                "locationName" to locationName)
    }
}