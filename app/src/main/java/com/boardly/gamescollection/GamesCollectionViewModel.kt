package com.boardly.gamescollection

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class GamesCollectionViewModel(private val gamesCollectionInteractor: GamesCollectionInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(GamesCollectionViewState())

    fun bind(gamesCollectionView: GamesCollectionView, collectionId: String) {


        val mergedObservable = Observable.merge(
                listOf(Observable.just(PartialGamesCollectionViewState.ProgressState)))
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