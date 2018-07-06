package com.boardly.addevent

import com.boardly.base.BaseInteractor
import com.boardly.retrofit.gamesearch.GameSearchService
import io.reactivex.Observable
import javax.inject.Inject

class AddEventInteractor @Inject constructor(private val gameSearchService: GameSearchService) : BaseInteractor() {

    fun fetchGameDetails(gameId: String): Observable<PartialAddEventViewState> {
        return gameSearchService.details(gameId)
                .map { PartialAddEventViewState.GameDetailsFetched(it.game) }
    }
}