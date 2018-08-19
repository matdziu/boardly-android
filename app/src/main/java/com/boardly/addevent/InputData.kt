package com.boardly.addevent

data class InputData(var eventName: String = "",
                     var maxPlayers: Int = 0,
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
                "maxPlayers" to maxPlayers,
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
}