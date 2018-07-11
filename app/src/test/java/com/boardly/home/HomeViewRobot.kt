package com.boardly.home

import com.boardly.base.BaseViewRobot
import com.boardly.filter.models.Filter
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class HomeViewRobot(homeViewModel: HomeViewModel) : BaseViewRobot<HomeViewState>() {

    private val filteredFetchSubject = PublishSubject.create<Filter>()

    private val homeView = object : HomeView {

        override fun render(homeViewState: HomeViewState) {
            renderedStates.add(homeViewState)
        }

        override fun filteredFetchTriggerEmitter(): Observable<Filter> = filteredFetchSubject
    }

    init {
        homeViewModel.bind(homeView)
    }

    fun emitFilteredFetchTrigger(filter: Filter) {
        filteredFetchSubject.onNext(filter)
    }
}