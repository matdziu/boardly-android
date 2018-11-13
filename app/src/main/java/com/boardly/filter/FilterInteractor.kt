package com.boardly.filter

import com.boardly.retrofit.gamesearch.GameSearchService
import com.boardly.retrofit.gamesearch.models.DetailsResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class FilterInteractor @Inject constructor(private val gameSearchService: GameSearchService) {

    fun fetchGameDetails(gameId: String): Observable<PartialFilterViewState> {
        return gameSearchService.boardGameDetails(gameId)
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn { DetailsResponse() }
                .map { PartialFilterViewState.GameDetailsFetched(it.game) }
    }
}