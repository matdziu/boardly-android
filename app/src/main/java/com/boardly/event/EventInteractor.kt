package com.boardly.event

import com.boardly.event.models.GamePickEvent
import com.boardly.event.network.EventService
import com.boardly.retrofit.gamesearch.GameSearchService
import com.boardly.retrofit.gamesearch.models.DetailsResponse
import io.reactivex.Observable
import javax.inject.Inject


class EventInteractor @Inject constructor(private val gameSearchService: GameSearchService,
                                          private val eventService: EventService) {

    fun fetchGameDetails(gamePickEvent: GamePickEvent): Observable<PartialEventViewState> {
        return gameSearchService.boardGameDetails(gamePickEvent.gameId)
                .onErrorReturn { DetailsResponse() }
                .map { PartialEventViewState.GameDetailsFetched(it.game, gamePickEvent.type) }
    }

    fun addEvent(inputData: InputData): Observable<PartialEventViewState> {
        return eventService.addEvent(inputData)
                .filter { it }
                .map { PartialEventViewState.SuccessState() }
    }

    fun editEvent(inputData: InputData): Observable<PartialEventViewState> {
        return eventService.editEvent(inputData)
                .filter { it }
                .map { PartialEventViewState.SuccessState() }
    }

    fun deleteEvent(eventId: String): Observable<PartialEventViewState> {
        return eventService.deleteEvent(eventId)
                .filter { it }
                .map { PartialEventViewState.RemovedState() }
    }
}