package com.boardly.home

import com.boardly.home.models.FilteredFetchData
import com.boardly.home.models.JoinEventData
import io.reactivex.Observable

interface HomeView {

    fun render(homeViewState: HomeViewState)

    fun filteredFetchTriggerEmitter(): Observable<FilteredFetchData>

    fun joinEventEmitter(): Observable<JoinEventData>

    fun locationProcessingEmitter(): Observable<Boolean>
}