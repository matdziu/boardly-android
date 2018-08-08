package com.boardly.myevents

import com.boardly.base.BaseServiceImpl
import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.subjects.PublishSubject

class MyEventsInteractor : BaseServiceImpl() {

    fun fetchEvents(): Observable<PartialMyEventsViewState> {
        val pendingEventsObservable = pendingEventIdsList().flatMap { events(it) }
        val acceptedEventsObservable = acceptedEventIdsList().flatMap { events(it) }
        val createdEventsObservable = createdEventIdsList().flatMap { events(it) }

        return Observable.zip(pendingEventsObservable,
                acceptedEventsObservable,
                createdEventsObservable,
                Function3<List<Event>, List<Event>, List<Event>, PartialMyEventsViewState> { pending, accepted, created ->
                    PartialMyEventsViewState.EventsFetchedState(
                            pending.map {
                                it.type = EventType.PENDING
                                it
                            }
                                    + accepted.map {
                                it.type = EventType.ACCEPTED
                                it
                            }
                                    + created.map {
                                it.type = EventType.CREATED
                                it
                            })
                })
    }

    private fun events(idsList: List<String>): Observable<List<Event>> {
        val resultSubject = PublishSubject.create<List<Event>>()
        val eventList = arrayListOf<Event>()

        if (idsList.isEmpty()) return Observable.just(eventList)

        for (id in idsList) {
            getSingleEventNode(id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue(Event::class.java)?.let {
                        it.eventId = id
                        eventList.add(it)
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