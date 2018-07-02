package com.boardly.home

import com.boardly.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class HomeViewRobot(homeViewModel: HomeViewModel) : BaseViewRobot<HomeViewState>() {

    private val initialFetchSubject = PublishSubject.create<Boolean>()

    private val homeView = object : HomeView {
        override fun render(homeViewState: HomeViewState) {
            renderedStates.add(homeViewState)
        }

        override fun emitInitialFetchTrigger(): Observable<Boolean> = initialFetchSubject
    }

    init {
        homeViewModel.bind(homeView)
    }

    fun emitInitalFetchTrigger() {
        initialFetchSubject.onNext(true)
    }
}