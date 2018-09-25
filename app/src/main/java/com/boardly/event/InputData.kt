package com.boardly.event

data class InputData(var eventId: String = "",
                     var eventName: String = "",
                     var description: String = "",
                     var gameName: String = "",
                     var gameId: String = "",
                     var gameName2: String = "",
                     var gameId2: String = "",
                     var gameName3: String = "",
                     var gameId3: String = "",
                     var gameImageUrl: String = "",
                     var gameImageUrl2: String = "",
                     var gameImageUrl3: String = "",
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
                "gameName2" to gameName2,
                "gameId2" to gameId2,
                "gameName3" to gameName3,
                "gameId3" to gameId3,
                "gameImageUrl" to gameImageUrl,
                "gameImageUrl2" to gameImageUrl2,
                "gameImageUrl3" to gameImageUrl3,
                "placeName" to placeName,
                "placeLatitude" to placeLatitude,
                "placeLongitude" to placeLongitude,
                "levelId" to levelId,
                "timestamp" to timestamp,
                "adminId" to adminId)
    }
}