package com.boardly.event

import com.boardly.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class EventViewRobot(eventViewModel: EventViewModel) : BaseViewRobot<EventViewState>() {

    private val inputDataSubject = PublishSubject.create<InputData>()
    private val gamePickEventSubject = PublishSubject.create<String>()
    private val placePickEventSubject = PublishSubject.create<Boolean>()

    private val eventView = object : EventView {
        override fun addEventEmitter(): Observable<InputData> = inputDataSubject

        override fun gamePickEventEmitter(): Observable<String> = gamePickEventSubject

        override fun placePickEventEmitter(): Observable<Boolean> = placePickEventSubject

        override fun render(eventViewState: EventViewState) {
            renderedStates.add(eventViewState)
        }
    }

    init {
        eventViewModel.bind(eventView)
    }

    fun emitInputData(inputData: InputData) {
        inputDataSubject.onNext(inputData)
    }

    fun pickGame(gameId: String) {
        gamePickEventSubject.onNext(gameId)
    }

    fun pickPlace() {
        placePickEventSubject.onNext(true)
    }
}