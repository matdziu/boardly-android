package com.boardly.pickgame

import com.boardly.base.BaseInteractor
import com.boardly.retrofit.gamesearch.GameSearchService
import io.reactivex.Observable
import javax.inject.Inject

class PickGameInteractor @Inject constructor(private val gameSearchService: GameSearchService)
    : BaseInteractor() {

    fun fetchSearchResults(query: String): Observable<PartialPickGameViewState> {
        return gameSearchService
                .search(query)
                .map { PartialPickGameViewState.ResultsFetchedState(it.games) }
    }
}