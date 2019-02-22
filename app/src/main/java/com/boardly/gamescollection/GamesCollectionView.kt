package com.boardly.gamescollection

import io.reactivex.Observable

interface GamesCollectionView {

    fun render(gamesCollectionViewState: GamesCollectionViewState)

    fun queryEmitter(): Observable<String>

    fun initialFetchTriggerEmitter(): Observable<Boolean>
}