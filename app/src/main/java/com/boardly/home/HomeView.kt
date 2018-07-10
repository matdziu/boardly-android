package com.boardly.home

import com.boardly.filter.models.Filter
import io.reactivex.Observable

interface HomeView {

    fun render(homeViewState: HomeViewState)

    fun emitFilteredFetchTrigger(): Observable<Filter>
}