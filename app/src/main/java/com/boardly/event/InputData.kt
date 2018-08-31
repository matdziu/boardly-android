package com.boardly.event

import com.boardly.common.events.models.Event

data class InputData(var eventId: String = "",
                     var eventName: String = "",
                     var description: String = "",
                     var gameName: String = "",
                     var gameId: String = "",
                     var gameImageUrl: String = "",
                     var placeName: String = "",
                     var placeLatitude: Double = 0.0,
                     var placeLongitude: Double = 0.0,
                     var levelId: String = "",
                     var timestamp: Long = 0,
                     var adminId: String = "") {

    fun toMap(): Map<String, Any> {
        return mapOf(
                "eventName" to eventName,
                "description" to description,
                "gameName" to gameName,
                "gameId" to gameId,
                "gameImageUrl" to gameImageUrl,
                "placeName" to placeName,
                "placeLatitude" to placeLatitude,
                "placeLongitude" to placeLongitude,
                "levelId" to levelId,
                "timestamp" to timestamp,
                "adminId" to adminId)
    }

    fun toEvent(): Event {
        return Event(
                eventId = eventId,
                eventName = eventName,
                description = description,
                gameName = gameName,
                gameId = gameId,
                gameImageUrl = gameImageUrl,
                placeName = placeName,
                placeLatitude = placeLatitude,
                placeLongitude = placeLongitude,
                levelId = levelId,
                timestamp = timestamp,
                adminId = adminId)
    }
}