package com.boardly.pickgame

import com.boardly.base.BaseInteractor
import com.boardly.retrofit.gamesearch.GameSearchService
import io.reactivex.Observable
import javax.inject.Inject

class PickGameInteractor @Inject constructor(private val gameSearchService: GameSearchService)
    : BaseInteractor() {

    private var latestQuery = ""

    fun fetchSearchResults(query: String): Observable<PartialPickGameViewState> {
        latestQuery = query
        return gameSearchService
                .search(query)
                .map { PartialPickGameViewState.ResultsFetchedState(it.games) }
                .cast(PartialPickGameViewState::class.java)
                .onErrorReturn { PartialPickGameViewState.ErrorState(it, query) }
                .filter { query == latestQuery }
    }
}