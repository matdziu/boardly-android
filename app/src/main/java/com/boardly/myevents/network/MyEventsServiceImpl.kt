package com.boardly.myevents.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.events.models.Event
import io.reactivex.Observable

class MyEventsServiceImpl : MyEventsService, BaseServiceImpl() {

    override fun getPendingEvents(): Observable<List<Event>> = pendingEventIdsList().flatMap { events(it) }

    override fun getAcceptedEvents(): Observable<List<Event>> = acceptedEventIdsList().flatMap { events(it) }

    override fun getCreatedEvents(): Observable<List<Event>> = createdEventIdsList().flatMap { events(it) }
}