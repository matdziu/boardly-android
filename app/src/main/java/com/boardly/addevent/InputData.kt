package com.boardly.addevent

data class InputData(var eventName: String = "",
                     var maxPlayers: Int = 0,
                     var description: String = "",
                     var gameId: String = "",
                     var placeLatitude: Double? = null,
                     var placeLongitude: Double? = null,
                     var levelId: String = "",
                     var timestamp: Long = 0)