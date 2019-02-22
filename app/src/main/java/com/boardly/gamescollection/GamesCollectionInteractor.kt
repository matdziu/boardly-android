package com.boardly.gamescollection

import com.boardly.gamescollection.network.GamesCollectionService
import com.boardly.retrofit.gameservice.GameService
import io.reactivex.Observable
import javax.inject.Inject

class GamesCollectionInteractor @Inject constructor(private val gameService: GameService,
                                                    private val gamesCollectionService: GamesCollectionService) {

    fun fetchGames(collectionId: String): Observable<PartialGamesCollectionViewState> {
        return gamesCollectionService.fetchGames(collectionId)
                .map { PartialGamesCollectionViewState.CollectionFetched(it) }
    }
}