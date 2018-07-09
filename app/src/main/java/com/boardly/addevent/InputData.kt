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
                     var timestamp: Long = 0)