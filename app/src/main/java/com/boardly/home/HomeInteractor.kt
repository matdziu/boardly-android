package com.boardly.home

import com.boardly.common.events.models.Event
import com.boardly.common.location.UserLocation
import com.boardly.extensions.isOlderThanOneHour
import com.boardly.home.models.JoinEventData
import com.boardly.home.network.HomeService
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class HomeInteractor @Inject constructor(private val homeService: HomeService) {

    init {
        homeService.sendClientNotificationToken()
    }

    fun fetchEvents(userLocation: UserLocation?, radius: Double, gameId: String): Observable<PartialHomeViewState> {
        return if (userLocation == null) Observable.just(PartialHomeViewState.EventListState(arrayListOf()))
        else Observable.zip(homeService.fetchUserEventIds(), homeService.fetchAllEvents(userLocation, radius, gameId),
                BiFunction<List<String>, List<Event>, PartialHomeViewState>
                { userEventIds, allEvents ->
                    val filteredEventList = allEvents.filter { !userEventIds.contains(it.eventId) && !isOlderThanOneHour(it.timestamp) }
                    PartialHomeViewState.EventListState(filteredEventList)
                })
    }

    fun joinEvent(joinEventData: JoinEventData): Observable<PartialHomeViewState> {
        return homeService.sendJoinRequest(joinEventData)
                .map { PartialHomeViewState.JoinRequestSent() }
    }
}