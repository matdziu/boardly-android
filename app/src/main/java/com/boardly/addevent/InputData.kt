package com.boardly.addevent

data class InputData(var eventName: String = "",
                     var maxPlayers: Int = 0,
                     var description: String = "",
                     var gameId: String = "",
                     var placeId: String = "",
                     var placeLatitude: Double = 0.0,
                     var placeLongitude: Double = 0.0,
                     var levelId: String = "",
                     var timestamp: Long = 0)