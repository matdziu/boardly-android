package com.boardly.myevents.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.events.models.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MyEventsServiceImpl : MyEventsService, BaseServiceImpl() {

    override fun getPendingEvents(): Observable<List<Event>> = pendingEventIdsList().flatMap { events(it) }

    override fun getAcceptedEvents(): Observable<List<Event>> = acceptedEventIdsList().flatMap { events(it) }

    override fun getCreatedEvents(): Observable<List<Event>> = createdEventIdsList().flatMap { events(it) }

    private fun events(idsList: List<String>): Observable<List<Event>> {
        val resultSubject = PublishSubject.create<List<Event>>()
        val eventList = arrayListOf<Event>()

        if (idsList.isEmpty()) return Observable.just(eventList)

        for (id in idsList) {
            getSingleEventNode(id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(Event::class.java)?.let {
                        it.eventId = id
                        eventList.add(0, it)
                    }
                    if (eventList.size == idsList.size) resultSubject.onNext(eventList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    resultSubject.onError(databaseError.toException())
                }
            })
        }

        return resultSubject
    }
}