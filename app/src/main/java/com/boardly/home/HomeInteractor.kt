package com.boardly.home

import com.boardly.common.events.models.Event
import com.boardly.common.events.models.EventType
import com.boardly.common.location.DistanceCalculator
import com.boardly.common.location.UserLocation
import com.boardly.extensions.isOlderThanOneDay
import com.boardly.home.models.JoinEventData
import com.boardly.home.network.HomeService
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class HomeInteractor @Inject constructor(private val homeService: HomeService,
                                         private val distanceCalculator: DistanceCalculator) {

    init {
        homeService.sendClientNotificationToken()
    }

    fun fetchEvents(userLocation: UserLocation?, radius: Double, gameId: String): Observable<PartialHomeViewState> {
        return if (userLocation == null) Observable.just(PartialHomeViewState.EventListState(arrayListOf()))
        else Observable.zip(
                homeService.fetchUserEvents(),
                homeService.fetchAllEvents(userLocation, radius, gameId),
                BiFunction<List<String>, List<Event>, PartialHomeViewState>
                { userEventsIds, allEvents ->
                    val filteredEventList = allEvents
                            .filter {
                                val userEventPredicate = isInsideRadius(userLocation, it.placeLatitude, it.placeLongitude, radius)
                                !isOlderThanOneDay(it.timestamp) && if (userEventsIds.contains(it.eventId)) userEventPredicate else true
                            }
                            .map {
                                if (userEventsIds.contains(it.eventId)) it.type = EventType.CREATED
                                it
                            }
                    PartialHomeViewState.EventListState(filteredEventList)
                })
    }

    private fun isInsideRadius(userLocation: UserLocation,
                               eventPlaceLatitude: Double,
                               eventPlaceLongitude: Double,
                               radius: Double): Boolean {
        return distanceCalculator.distanceBetween(
                userLocation.latitude,
                userLocation.longitude,
                eventPlaceLatitude,
                eventPlaceLongitude) < radius * 1000
    }

    fun joinEvent(joinEventData: JoinEventData): Observable<PartialHomeViewState> {
        return homeService.sendJoinRequest(joinEventData)
                .map { PartialHomeViewState.JoinRequestSent(false) }
                .cast(PartialHomeViewState::class.java)
                .startWith(PartialHomeViewState.JoinRequestSent())
    }
}