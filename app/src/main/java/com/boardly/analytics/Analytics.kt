package com.boardly.analytics

interface Analytics {

    fun logEventAddedEvent(gameId: String, placeLatitude: Double, placeLongitude: Double)

    fun logJoinRequestSentEvent()

    fun logJoinRequestAcceptedEvent()
}