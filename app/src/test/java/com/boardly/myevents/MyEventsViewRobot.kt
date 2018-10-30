package com.boardly.myevents

import com.boardly.base.BaseViewRobot
import com.boardly.home.models.JoinEventData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MyEventsViewRobot(myEventsViewModel: MyEventsViewModel) : BaseViewRobot<MyEventsViewState>() {

    private val fetchEventsSubject = PublishSubject.create<Boolean>()
    private val joinEventSubject = PublishSubject.create<JoinEventData>()

    private val myEventsView = object : MyEventsView {
        override fun joinEventEmitter(): Observable<JoinEventData> = joinEventSubject

        override fun render(myEventsViewState: MyEventsViewState) {
            renderedStates.add(myEventsViewState)
        }

        override fun fetchEventsTriggerEmitter(): Observable<Boolean> = fetchEventsSubject
    }

    init {
        myEventsViewModel.bind(myEventsView)
    }

    fun triggerEventsFetching(showProgress: Boolean) {
        fetchEventsSubject.onNext(showProgress)
    }

    fun joinEvent(joinEventData: JoinEventData) {
        joinEventSubject.onNext(joinEventData)
    }
}