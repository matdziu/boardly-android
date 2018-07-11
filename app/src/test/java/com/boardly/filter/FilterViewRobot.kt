package com.boardly.filter

import com.boardly.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class FilterViewRobot(filterViewModel: FilterViewModel) : BaseViewRobot<FilterViewState>() {

    private val gameIdSubject = PublishSubject.create<String>()

    private val filterView = object : FilterView {

        override fun render(filterViewState: FilterViewState) {
            renderedStates.add(filterViewState)
        }

        override fun gameIdEmitter(): Observable<String> = gameIdSubject
    }

    init {
        filterViewModel.bind(filterView)
    }

    fun emitGameId(gameId: String) {
        gameIdSubject.onNext(gameId)
    }
}