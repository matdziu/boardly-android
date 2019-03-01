package com.boardly.gamescollection

import android.arch.lifecycle.ViewModel
import com.boardly.gamescollection.models.CollectionGame
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class GamesCollectionViewModel(private val gamesCollectionInteractor: GamesCollectionInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(GamesCollectionViewState())
    private var currentCollectionGames = listOf<CollectionGame>()

    fun bind(gamesCollectionView: GamesCollectionView, collectionId: String) {
        val initialFetchObservable = gamesCollectionView.initialFetchTriggerEmitter()
                .flatMap {
                    if (it) gamesCollectionInteractor.fetchGames(collectionId).startWith(PartialGamesCollectionViewState.ProgressState)
                    else gamesCollectionInteractor.fetchGames(collectionId)
                }
                .doOnNext { if (it is PartialGamesCollectionViewState.CollectionFetched) currentCollectionGames = it.games }

        val newGameObservable = gamesCollectionView.newGameEmitter()
                .flatMap { gamesCollectionInteractor.addGame(collectionId, it, currentCollectionGames.size) }

        val deleteGameObservable = gamesCollectionView.deleteGameEmitter()
                .flatMap { gamesCollectionInteractor.deleteGame(collectionId, it) }

        val queryObservable = gamesCollectionView.queryEmitter()
                .map { query ->
                    val formattedQuery = query.trim().toLowerCase()
                    PartialGamesCollectionViewState.CollectionFetched(
                            currentCollectionGames.filter { it.name.toLowerCase().contains(formattedQuery) })
                }

        val mergedObservable = Observable.merge(
                listOf(initialFetchObservable,
                        queryObservable,
                        newGameObservable,
                        deleteGameObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { gamesCollectionView.render(it) })
    }

    private fun reduce(previousState: GamesCollectionViewState, partialState: PartialGamesCollectionViewState)
            : GamesCollectionViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}