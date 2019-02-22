package com.boardly.gamescollection

import com.boardly.gamescollection.models.CollectionGame
import io.reactivex.Observable

interface GamesCollectionView {

    fun render(gamesCollectionViewState: GamesCollectionViewState)

    fun queryEmitter(): Observable<String>

    fun initialFetchTriggerEmitter(): Observable<Boolean>

    fun newGameEmitter(): Observable<CollectionGame>

    fun deleteGameEmitter(): Observable<String>

    fun emitGameDeletion(gameId: String)
}