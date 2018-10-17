package com.boardly.home

import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.boardly.common.location.UserLocation
import com.boardly.extensions.isOlderThanOneDay
import com.boardly.home.models.JoinEventData
import com.boardly.home.network.HomeService
import io.reactivex.Observable
import io.reactivex.functions.Function3
import javax.inject.Inject

class HomeInteractor @Inject constructor(private val homeService: HomeService) {

    init {
        homeService.sendClientNotificationToken()
    }

    fun fetchEvents(userLocation: UserLocation?, radius: Double, gameId: String): Observable<PartialHomeViewState> {
        return if (userLocation == null) Observable.just(PartialHomeViewState.EventListState(arrayListOf()))
        else Observable.zip(
                homeService.fetchUserEvents(),
                homeService.fetchCreatedEvents(),
                homeService.fetchAllEvents(userLocation, radius, gameId),
                Function3<List<String>, List<Event>, List<Event>, PartialHomeViewState>
                { userEventsIds, createdEvents, allEvents ->
                    val filteredEventList = allEvents
                            .filter { !userEventsIds.contains(it.eventId) && !isOlderThanOneDay(it.timestamp) }
                    val createdEventsWithType = createdEvents
                            .filter {
                                !isOlderThanOneDay(it.timestamp)
                                        && (gameId.isEmpty() ||
                                        it.gameId == gameId ||
                                        it.gameId2 == gameId ||
                                        it.gameId3 == gameId)
                            }
                            .map { it.type = EventType.CREATED; it }
                    PartialHomeViewState.EventListState(filteredEventList + createdEventsWithType)
                })
    }

    fun joinEvent(joinEventData: JoinEventData): Observable<PartialHomeViewState> {
        return homeService.sendJoinRequest(joinEventData)
                .map { PartialHomeViewState.JoinRequestSent(false) }
                .cast(PartialHomeViewState::class.java)
                .startWith(PartialHomeViewState.JoinRequestSent())
    }
}