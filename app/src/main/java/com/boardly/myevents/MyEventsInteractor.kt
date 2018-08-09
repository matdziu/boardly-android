package com.boardly.myevents

import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.boardly.myevents.network.MyEventsService
import io.reactivex.Observable
import io.reactivex.functions.Function3
import javax.inject.Inject

class MyEventsInteractor @Inject constructor(private val myEventsService: MyEventsService) {

    fun fetchEvents(): Observable<PartialMyEventsViewState> {
        val pendingEventsObservable = myEventsService.getPendingEvents()
        val acceptedEventsObservable = myEventsService.getAcceptedEvents()
        val createdEventsObservable = myEventsService.getCreatedEvents()

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
}