package com.boardly.home.network

import com.boardly.common.events.models.Event
import com.boardly.home.models.JoinEventData
import com.boardly.home.models.UserLocation
import io.reactivex.Observable

interface HomeService {

    fun fetchAllEvents(userLocation: UserLocation, radius: Double, gameId: String): Observable<List<Event>>

    fun fetchUserEventIds(): Observable<List<String>>

    fun sendJoinRequest(joinEventData: JoinEventData): Observable<Boolean>
}