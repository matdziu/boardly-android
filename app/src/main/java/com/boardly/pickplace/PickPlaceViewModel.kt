package com.boardly.pickplace

import android.arch.lifecycle.ViewModel
import com.boardly.common.search.SearchResultData
import com.boardly.constants.LATITUDE
import com.boardly.constants.LONGITUDE
import com.boardly.retrofit.places.SearchPlacesUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class PickPlaceViewModel(private val searchPlacesUseCase: SearchPlacesUseCase) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(PickPlaceViewState())

    fun bind(pickPlaceView: PickPlaceView) {
        pickPlaceView.queryEmitter()
                .filter { it.length > 1 }
                .flatMap {
                    searchPlacesUseCase.search(it)
                            .map { placeSearchResult ->
                                PartialPickPlaceViewState.ResultsFetchedState(placeSearchResult.map { searchResponse ->
                                    SearchResultData(
                                            id = "${searchResponse.name}, ${searchResponse.latitude}, ${searchResponse.longitude}",
                                            title = searchResponse.name,
                                            subtitle = "",
                                            additionalInfo = hashMapOf(
                                                    LATITUDE to searchResponse.latitude.toString(),
                                                    LONGITUDE to searchResponse.longitude.toString()))
                                })
                            }
                            .cast(PartialPickPlaceViewState::class.java)
                            .startWith(PartialPickPlaceViewState.ProgressState)
                }
                .scan(stateSubject.value, BiFunction(this::reduce))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateSubject)

        compositeDisposable.add(stateSubject.subscribe { pickPlaceView.render(it) })
    }

    private fun reduce(previousState: PickPlaceViewState, partialState: PartialPickPlaceViewState): PickPlaceViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}