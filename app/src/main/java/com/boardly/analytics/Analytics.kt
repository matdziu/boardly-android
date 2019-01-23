package com.boardly.analytics

interface Analytics {

    fun logEventAddedEvent(gameId: String,
                           gameId2: String,
                           gameId3: String,
                           placeLatitude: Double,
                           placeLongitude: Double)

    fun logJoinRequestSentEvent()

    fun logJoinRequestAcceptedEvent()
}