package com.boardly.notify.models

data class NotifySettings(var radius: Double = 50.0,
                          var gameId: String = "",
                          var gameName: String = "",
                          var userLatitude: Double? = null,
                          var userLongitude: Double? = null,
                          var locationName: String = "") {

    fun toMap(): Map<String, Any?> {
        return mapOf(
                "radius" to radius,
                "gameId" to gameId,
                "gameName" to gameName,
                "locationName" to locationName,
                "userLatitude" to userLatitude,
                "userLongitude" to userLongitude)
    }
}