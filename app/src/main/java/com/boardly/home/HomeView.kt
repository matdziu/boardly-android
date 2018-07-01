package com.boardly.home

import io.reactivex.Observable

interface HomeView {

    fun render(homeViewState: HomeViewState)

    fun emitInitialFetchTrigger(): Observable<Boolean>
}