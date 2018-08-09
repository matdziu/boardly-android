package com.boardly.myevents.network

import com.boardly.common.events.models.Event
import io.reactivex.Observable

interface MyEventsService {

    fun getPendingEvents(): Observable<List<Event>>

    fun getAcceptedEvents(): Observable<List<Event>>

    fun getCreatedEvents(): Observable<List<Event>>
}