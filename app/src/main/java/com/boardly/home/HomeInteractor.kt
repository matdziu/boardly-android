package com.boardly.home

import com.boardly.base.BaseInteractor
import com.boardly.home.models.Event
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class HomeInteractor : BaseInteractor() {

    fun fetchEvents(radius: Double, gameId: String): Observable<PartialHomeViewState> {
        return Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { PartialHomeViewState.EventsFetchedState(listOf(Event())) }
    }
}