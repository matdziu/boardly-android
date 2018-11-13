package com.boardly.event

import com.boardly.event.models.GamePickEvent
import com.boardly.event.network.EventService
import com.boardly.retrofit.gameservice.GameService
import com.boardly.retrofit.gameservice.models.DetailsResponse
import io.reactivex.Observable
import javax.inject.Inject


class EventInteractor @Inject constructor(private val gameService: GameService,
                                          private val eventService: EventService) {

    fun fetchGameDetails(gamePickEvent: GamePickEvent): Observable<PartialEventViewState> {
        return gameService.gameDetails(gamePickEvent.gameId)
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