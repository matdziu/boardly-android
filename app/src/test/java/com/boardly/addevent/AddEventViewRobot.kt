package com.boardly.addevent

import com.boardly.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AddEventViewRobot(addEventViewModel: AddEventViewModel) : BaseViewRobot<AddEventViewState>() {

    private val inputDataSubject = PublishSubject.create<InputData>()
    private val gamePickEventSubject = PublishSubject.create<String>()
    private val placePickEventSubject = PublishSubject.create<Boolean>()

    private val addEventView = object : AddEventView {
        override fun inputDataEmitter(): Observable<InputData> = inputDataSubject

        override fun gamePickEventEmitter(): Observable<String> = gamePickEventSubject

        override fun placePickEventEmitter(): Observable<Boolean> = placePickEventSubject

        override fun render(addEventViewState: AddEventViewState) {
            renderedStates.add(addEventViewState)
        }
    }

    init {
        addEventViewModel.bind(addEventView)
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