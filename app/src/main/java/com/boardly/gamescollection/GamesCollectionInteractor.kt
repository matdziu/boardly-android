package com.boardly.gamescollection

import com.boardly.gamescollection.models.CollectionGame
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

    fun addGame(collectionId: String, collectionGame: CollectionGame): Observable<PartialGamesCollectionViewState> {
        return gameService.gameDetails(collectionGame.id)
                .flatMap {
                    collectionGame.imageUrl = it.game.image
                    gamesCollectionService.addGame(collectionId, collectionGame)
                }
                .flatMap { emitSuccessState() }
    }

    fun deleteGame(collectionId: String, gameId: String): Observable<PartialGamesCollectionViewState> {
        return gamesCollectionService.deleteGame(collectionId, gameId)
                .flatMap { emitSuccessState() }
    }

    private fun emitSuccessState(): Observable<PartialGamesCollectionViewState> {
        return Observable.just(PartialGamesCollectionViewState.SuccessState(false))
                .startWith(PartialGamesCollectionViewState.SuccessState())
                .cast(PartialGamesCollectionViewState::class.java)
    }
}