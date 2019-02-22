package com.boardly.gamescollection.network

import com.boardly.gamescollection.models.CollectionGame
import io.reactivex.Observable

interface GamesCollectionService {

    fun fetchGames(collectionId: String): Observable<List<CollectionGame>>

    fun addGame(collectionId: String, game: CollectionGame): Observable<Boolean>

    fun editGame(collectionId: String, game: CollectionGame): Observable<Boolean>

    fun deleteGame(collectionId: String, gameId: String): Observable<Boolean>
}