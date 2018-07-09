package com.boardly.home.models

data class Event(var eventId: String = "",
                 val eventName: String = "",
                 val gameName: String = "",
                 val levelId: String = "",
                 val placeName: String = "",
                 val timestamp: Long = 0,
                 val maxPlayers: Int = 0,
                 val currentNumberOfPlayers: Int = 0,
                 val gameImageUrl: String = "",
                 val description: String = "")