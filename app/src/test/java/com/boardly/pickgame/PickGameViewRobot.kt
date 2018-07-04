package com.boardly.pickgame

import com.boardly.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PickGameViewRobot(pickGameViewModel: PickGameViewModel) : BaseViewRobot<PickGameViewState>() {

    private val emitQuerySubject = PublishSubject.create<String>()

    private val pickGameView = object : PickGameView {
        override fun emitQuery(): Observable<String> = emitQuerySubject

        override fun render(pickGameViewState: PickGameViewState) {
            renderedStates.add(pickGameViewState)
        }
    }

    init {
        pickGameViewModel.bind(pickGameView)
    }

    fun emitQuery(query: String) {
        emitQuerySubject.onNext(query)
    }
}