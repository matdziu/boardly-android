package com.boardly.home

import com.boardly.base.BaseViewRobot
import com.boardly.filter.models.Filter
import com.boardly.home.models.JoinEventData
import com.boardly.home.models.UserLocation
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class HomeViewRobot(homeViewModel: HomeViewModel) : BaseViewRobot<HomeViewState>() {

    private val filteredFetchSubject = PublishSubject.create<Pair<UserLocation, Filter>>()
    private val joinEventSubject = PublishSubject.create<JoinEventData>()
    private val locationProcessingSubject = PublishSubject.create<Boolean>()

    private val homeView = object : HomeView {

        override fun locationProcessingEmitter(): Observable<Boolean> = locationProcessingSubject

        override fun joinEventEmitter(): Observable<JoinEventData> = joinEventSubject

        override fun render(homeViewState: HomeViewState) {
            renderedStates.add(homeViewState)
        }

        override fun filteredFetchTriggerEmitter(): Observable<Pair<UserLocation, Filter>> = filteredFetchSubject
    }

    init {
        homeViewModel.bind(homeView)
    }

    fun emitFilteredFetchTrigger(userLocation: UserLocation, filter: Filter) {
        filteredFetchSubject.onNext(Pair(userLocation, filter))
    }

    fun joinEvent(joinEventData: JoinEventData) {
        joinEventSubject.onNext(joinEventData)
    }

    fun processLocation() {
        locationProcessingSubject.onNext(true)
    }
}