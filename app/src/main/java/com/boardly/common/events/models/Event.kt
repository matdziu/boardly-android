package com.boardly.common.events.models

data class Event(var eventId: String = "",
                 val eventName: String = "",
                 val gameId: String = "",
                 val gameName: String = "",
                 val levelId: String = "",
                 val placeName: String = "",
                 val timestamp: Long = 0,
                 val maxPlayers: Int = 0,
                 val currentNumberOfPlayers: Int = 0,
                 val gameImageUrl: String = "",
                 val description: String = "",
                 var type: EventType = EventType.DEFAULT)

enum class EventType {
    CREATED, ACCEPTED, PENDING, DEFAULT
}