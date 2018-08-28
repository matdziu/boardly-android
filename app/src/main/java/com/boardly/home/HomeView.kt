package com.boardly.home

import com.boardly.filter.models.Filter
import com.boardly.home.models.JoinEventData
import com.boardly.home.models.UserLocation
import io.reactivex.Observable

interface HomeView {

    fun render(homeViewState: HomeViewState)

    fun filteredFetchTriggerEmitter(): Observable<Pair<UserLocation, Filter>>

    fun joinEventEmitter(): Observable<JoinEventData>

    fun locationProcessingEmitter(): Observable<Boolean>
}