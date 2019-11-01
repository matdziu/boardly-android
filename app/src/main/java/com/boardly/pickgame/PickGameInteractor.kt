package com.boardly.pickgame

import com.boardly.retrofit.gameservice.BoardGameGeekService
import io.reactivex.Observable
import javax.inject.Inject

@Deprecated("Can't use BGG API")
class PickGameInteractor @Inject constructor(private val boardGameGeekService: BoardGameGeekService) {

    private var latestQuery = ""

    fun fetchSearchResults(query: String): Observable<PartialPickGameViewState> {
        latestQuery = query
        return boardGameGeekService
                .search(query)
                .map { PartialPickGameViewState.ResultsFetchedState(it.games) }
                .cast(PartialPickGameViewState::class.java)
                .onErrorReturn { PartialPickGameViewState.ErrorState(it, query) }
                .filter { query == latestQuery }
    }
}