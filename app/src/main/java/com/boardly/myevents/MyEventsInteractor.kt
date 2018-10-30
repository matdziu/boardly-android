package com.boardly.myevents

import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.boardly.extensions.isOlderThanOneDay
import com.boardly.home.models.JoinEventData
import com.boardly.myevents.network.MyEventsService
import io.reactivex.Observable
import io.reactivex.functions.Function4
import javax.inject.Inject

class MyEventsInteractor @Inject constructor(private val myEventsService: MyEventsService) {

    fun fetchEvents(): Observable<PartialMyEventsViewState> {
        val pendingEventsObservable = myEventsService.getPendingEvents()
        val acceptedEventsObservable = myEventsService.getAcceptedEvents()
        val createdEventsObservable = myEventsService.getCreatedEvents()
        val interestingEventsObservable = myEventsService.getInterestingEvents()

        return Observable.zip(pendingEventsObservable,
                acceptedEventsObservable,
                createdEventsObservable,
                interestingEventsObservable,
                Function4<List<Event>, List<Event>, List<Event>, List<Event>, PartialMyEventsViewState>
                { pending, accepted, created, interesting ->
                    val other = pending + accepted + created
                    PartialMyEventsViewState.EventsFetchedState(
                            accepted.map { it.type = EventType.ACCEPTED;it }.filter { !isOlderThanOneDay(it.timestamp) },
                            pending.map { it.type = EventType.PENDING; it }.filter { !isOlderThanOneDay(it.timestamp) },
                            created.map { it.type = EventType.CREATED; it }.filter { !isOlderThanOneDay(it.timestamp) },
                            interesting.filter { interestingEvent ->
                                !isOlderThanOneDay(interestingEvent.timestamp)
                                        && other.find { it.eventId == interestingEvent.eventId } == null
                            }.distinct())
                })
    }

    fun joinEvent(joinEventData: JoinEventData): Observable<PartialMyEventsViewState> {
        return myEventsService.sendJoinRequest(joinEventData)
                .map { PartialMyEventsViewState.JoinRequestSent(false) }
                .cast(PartialMyEventsViewState::class.java)
                .startWith(PartialMyEventsViewState.JoinRequestSent())
    }
}