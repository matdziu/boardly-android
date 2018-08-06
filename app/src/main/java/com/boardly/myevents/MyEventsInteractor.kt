package com.boardly.myevents

import com.boardly.base.BaseInteractor
import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.subjects.PublishSubject

class MyEventsInteractor : BaseInteractor() {

    fun fetchEvents(): Observable<PartialMyEventsViewState> {
        val pendingEventsObservable = pendingEventIdsList().flatMap { events(it) }
        val acceptedEventsObservable = acceptedEventIdsList().flatMap { events(it) }
        val mineEventsObservable = mineEventIdsList().flatMap { events(it) }

        return Observable.zip(pendingEventsObservable,
                acceptedEventsObservable,
                mineEventsObservable,
                Function3<List<Event>, List<Event>, List<Event>, PartialMyEventsViewState> { pending, accepted, mine ->
                    PartialMyEventsViewState.EventsFetchedState(
                            pending.map {
                                it.type = EventType.PENDING
                                it
                            }
                                    + accepted.map {
                                it.type = EventType.ACCEPTED
                                it
                            }
                                    + mine.map {
                                it.type = EventType.MINE
                                it
                            })
                })
    }

    private fun pendingEventIdsList(): Observable<List<String>> {
        return idsList(getUserPendingEventsNodeRef(currentUserId))
    }

    private fun acceptedEventIdsList(): Observable<List<String>> {
        return idsList(getUserAcceptedEventsNodeRef(currentUserId))
    }

    private fun mineEventIdsList(): Observable<List<String>> {
        return idsList(getUserMineEventsNodeRef(currentUserId))
    }

    private fun idsList(idsDatabaseReference: DatabaseReference): Observable<List<String>> {
        val resultSubject = PublishSubject.create<List<String>>()

        idsDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val idsList = arrayListOf<String>()
                for (childSnapshot in dataSnapshot.children) {
                    childSnapshot.getValue(String::class.java)?.let { idsList.add(it) }
                }
                resultSubject.onNext(idsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onError(databaseError.toException())
            }
        })

        return resultSubject
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