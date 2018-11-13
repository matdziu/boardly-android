package com.boardly.filter

import com.boardly.retrofit.gameservice.GameService
import com.boardly.retrofit.gameservice.models.DetailsResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class FilterInteractor @Inject constructor(private val gameService: GameService) {

    fun fetchGameDetails(gameId: String): Observable<PartialFilterViewState> {
        return gameService.gameDetails(gameId)
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn { DetailsResponse() }
                .map { PartialFilterViewState.GameDetailsFetched(it.game) }
    }
}