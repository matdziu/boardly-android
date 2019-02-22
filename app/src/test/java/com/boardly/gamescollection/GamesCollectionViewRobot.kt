package com.boardly.gamescollection

import com.boardly.base.BaseViewRobot
import com.boardly.gamescollection.models.CollectionGame
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class GamesCollectionViewRobot(gamesCollectionViewModel: GamesCollectionViewModel) : BaseViewRobot<GamesCollectionViewState>() {

    private val queryEmitterSubject = PublishSubject.create<String>()
    private val initialFetchTriggerSubject = PublishSubject.create<Boolean>()
    private val newGameSubject = PublishSubject.create<CollectionGame>()
    private val deleteGameSubject = PublishSubject.create<String>()

    private val gamesCollectionView = object : GamesCollectionView {
        override fun render(gamesCollectionViewState: GamesCollectionViewState) {
            renderedStates.add(gamesCollectionViewState)
        }

        override fun queryEmitter(): Observable<String> = queryEmitterSubject

        override fun initialFetchTriggerEmitter(): Observable<Boolean> = initialFetchTriggerSubject

        override fun newGameEmitter(): Observable<CollectionGame> = newGameSubject

        override fun deleteGameEmitter(): Observable<String> = deleteGameSubject

        override fun emitGameDeletion(gameId: String) {
            deleteGameSubject.onNext(gameId)
        }
    }

    init {
        gamesCollectionViewModel.bind(gamesCollectionView, "testCollectionId")
    }

    fun emitQuery(query: String) {
        queryEmitterSubject.onNext(query)
    }

    fun fetchInitialData(showProgress: Boolean) {
        initialFetchTriggerSubject.onNext(showProgress)
    }

    fun addGame(game: CollectionGame) {
        newGameSubject.onNext(game)
    }

    fun deleteGame(gameId: String) {
        deleteGameSubject.onNext(gameId)
    }
}