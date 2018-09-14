package com.boardly.home

import com.boardly.base.BaseViewRobot
import com.boardly.home.models.FilteredFetchData
import com.boardly.home.models.JoinEventData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class HomeViewRobot(homeViewModel: HomeViewModel) : BaseViewRobot<HomeViewState>() {

    private val filteredFetchSubject = PublishSubject.create<FilteredFetchData>()
    private val joinEventSubject = PublishSubject.create<JoinEventData>()
    private val locationProcessingSubject = PublishSubject.create<Boolean>()

    private val homeView = object : HomeView {

        override fun locationProcessingEmitter(): Observable<Boolean> = locationProcessingSubject

        override fun joinEventEmitter(): Observable<JoinEventData> = joinEventSubject

        override fun render(homeViewState: HomeViewState) {
            renderedStates.add(homeViewState)
        }

        override fun filteredFetchTriggerEmitter(): Observable<FilteredFetchData> = filteredFetchSubject
    }

    init {
        homeViewModel.bind(homeView)
    }

    fun emitFilteredFetchTrigger(filteredFetchData: FilteredFetchData) {
        filteredFetchSubject.onNext(filteredFetchData)
    }

    fun joinEvent(joinEventData: JoinEventData) {
        joinEventSubject.onNext(joinEventData)
    }

    fun processLocation() {
        locationProcessingSubject.onNext(true)
    }
}