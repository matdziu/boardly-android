package com.boardly.analytics

interface Analytics {

    fun logSignUpEvent()

    fun logEventAddedEvent()

    fun logJoinRequestSentEvent()

    fun logJoinRequestAcceptedEvent()
}