package com.boardly.addevent

import com.boardly.addevent.network.AddEventService
import com.boardly.retrofit.gamesearch.GameSearchService
import com.boardly.retrofit.gamesearch.models.DetailsResponse
import io.reactivex.Observable
import javax.inject.Inject


class AddEventInteractor @Inject constructor(private val gameSearchService: GameSearchService,
                                             private val addEventService: AddEventService) {

    fun fetchGameDetails(gameId: String): Observable<PartialAddEventViewState> {
        return gameSearchService.details(gameId)
                .onErrorReturn { DetailsResponse() }
                .map { PartialAddEventViewState.GameDetailsFetched(it.game) }
    }

    fun addEvent(inputData: InputData): Observable<PartialAddEventViewState> {
        return addEventService.addEvent(inputData)
                .filter { it }
                .map { PartialAddEventViewState.SuccessState() }
    }
}