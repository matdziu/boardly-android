package com.boardly.event

import com.boardly.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class EventViewRobot(eventViewModel: EventViewModel) : BaseViewRobot<EventViewState>() {

    private val addEventSubject = PublishSubject.create<InputData>()
    private val gamePickEventSubject = PublishSubject.create<String>()
    private val placePickEventSubject = PublishSubject.create<Boolean>()
    private val editEventSubject = PublishSubject.create<InputData>()
    private val deleteEventSubject = PublishSubject.create<String>()

    private val eventView = object : EventView {
        override fun deleteEventEmitter(): Observable<String> = deleteEventSubject

        override fun editEventEmitter(): Observable<InputData> = editEventSubject

        override fun addEventEmitter(): Observable<InputData> = addEventSubject

        override fun gamePickEventEmitter(): Observable<String> = gamePickEventSubject

        override fun placePickEventEmitter(): Observable<Boolean> = placePickEventSubject

        override fun render(eventViewState: EventViewState) {
            renderedStates.add(eventViewState)
        }
    }

    init {
        eventViewModel.bind(eventView)
    }

    fun addEvent(inputData: InputData) {
        addEventSubject.onNext(inputData)
    }

    fun pickGame(gameId: String) {
        gamePickEventSubject.onNext(gameId)
    }

    fun pickPlace() {
        placePickEventSubject.onNext(true)
    }

    fun editEvent(inputData: InputData) {
        editEventSubject.onNext(inputData)
    }

    fun deleteEvent(eventId: String) {
        deleteEventSubject.onNext(eventId)
    }
}