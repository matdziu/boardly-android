package com.boardly.common.events.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(var eventId: String = "",
                 val eventName: String = "",
                 val gameId: String = "",
                 val gameName: String = "",
                 val levelId: String = "",
                 val placeName: String = "",
                 val timestamp: Long = 0,
                 val gameImageUrl: String = "",
                 val description: String = "",
                 val adminId: String = "",
                 var type: EventType = EventType.DEFAULT) : Parcelable

enum class EventType {
    CREATED, ACCEPTED, PENDING, DEFAULT
}