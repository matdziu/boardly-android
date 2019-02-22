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

    fun bind(gamesCollectionView: GamesCollectionView, collectionId: String) {
        val initialFetchObservable = gamesCollectionView.initialFetchTriggerEmitter()
                .map { PartialGamesCollectionViewState.ProgressState }

        val mergedObservable = Observable.merge(
                listOf(initialFetchObservable))
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