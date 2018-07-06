package com.boardly.addevent

import com.boardly.base.BaseInteractor
import com.boardly.retrofit.gamesearch.GameSearchService
import com.boardly.retrofit.gamesearch.models.DetailsResponse
import io.reactivex.Observable
import javax.inject.Inject

class AddEventInteractor @Inject constructor(private val gameSearchService: GameSearchService) : BaseInteractor() {

    fun fetchGameDetails(gameId: String): Observable<PartialAddEventViewState> {
        return gameSearchService.details(gameId)
                .onErrorReturn { DetailsResponse() }
                .map { PartialAddEventViewState.GameDetailsFetched(it.game) }
    }
}