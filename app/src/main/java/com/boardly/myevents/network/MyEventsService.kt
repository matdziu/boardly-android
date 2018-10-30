package com.boardly.myevents.network

import com.boardly.common.events.models.Event
import com.boardly.home.models.JoinEventData
import io.reactivex.Observable

interface MyEventsService {

    fun getPendingEvents(): Observable<List<Event>>

    fun getAcceptedEvents(): Observable<List<Event>>

    fun getCreatedEvents(): Observable<List<Event>>

    fun getInterestingEvents(): Observable<List<Event>>

    fun sendJoinRequest(joinEventData: JoinEventData): Observable<Boolean>
}