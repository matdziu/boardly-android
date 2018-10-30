package com.boardly.home

import com.boardly.base.joinevent.BaseJoinEventView
import com.boardly.home.models.FilteredFetchData
import io.reactivex.Observable

interface HomeView : BaseJoinEventView {

    fun render(homeViewState: HomeViewState)

    fun filteredFetchTriggerEmitter(): Observable<FilteredFetchData>

    fun locationProcessingEmitter(): Observable<Boolean>
}