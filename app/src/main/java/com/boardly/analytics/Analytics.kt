package com.boardly.analytics

interface Analytics {

    fun logEventAddedEvent()

    fun logJoinRequestSentEvent()

    fun logJoinRequestAcceptedEvent()
}