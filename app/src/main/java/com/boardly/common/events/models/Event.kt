package com.boardly.common.events.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(var eventId: String = "",
                 val eventName: String = "",
                 var gameName: String = "",
                 var gameId: String = "",
                 var gameName2: String = "",
                 var gameId2: String = "",
                 var gameName3: String = "",
                 var gameId3: String = "",
                 var gameImageUrl: String = "",
                 var gameImageUrl2: String = "",
                 var gameImageUrl3: String = "",
                 val placeName: String = "",
                 val timestamp: Long = 0,
                 val description: String = "",
                 val placeLatitude: Double = 0.0,
                 val placeLongitude: Double = 0.0,
                 val adminId: String = "",
                 var type: EventType = EventType.DEFAULT) : Parcelable

enum class EventType {
    CREATED, ACCEPTED, PENDING, DEFAULT
}