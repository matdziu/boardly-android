package com.boardly.gamescollection.network

import com.boardly.gamescollection.models.CollectionGame
import io.reactivex.Observable

class GamesCollectionServiceImpl : GamesCollectionService {

    override fun fetchGames(collectionId: String): Observable<List<CollectionGame>> {
        return Observable.empty()
    }

    override fun addGame(collectionId: String, game: CollectionGame): Observable<Boolean> {
        return Observable.empty()
    }

    override fun editGame(collectionId: String, game: CollectionGame): Observable<Boolean> {
        return Observable.empty()
    }

    override fun deleteGame(collectionId: String, gameId: String): Observable<Boolean> {
        return Observable.empty()
    }
}