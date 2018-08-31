package com.boardly.event

import com.boardly.event.network.EventService
import com.boardly.retrofit.gamesearch.GameSearchService
import com.boardly.retrofit.gamesearch.models.DetailsResponse
import io.reactivex.Observable
import javax.inject.Inject


class EventInteractor @Inject constructor(private val gameSearchService: GameSearchService,
                                          private val eventService: EventService) {

    fun fetchGameDetails(gameId: String): Observable<PartialEventViewState> {
        return gameSearchService.details(gameId)
                .onErrorReturn { DetailsResponse() }
                .map { PartialEventViewState.GameDetailsFetched(it.game) }
    }

    fun addEvent(inputData: InputData): Observable<PartialEventViewState> {
        return eventService.addEvent(inputData)
                .filter { it }
                .map { PartialEventViewState.SuccessState() }
    }
}