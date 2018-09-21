package com.boardly.eventdetails.players

import com.boardly.base.BaseViewRobot
import com.boardly.base.eventdetails.models.RateInput
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PlayersViewRobot(private val playersViewModel: PlayersViewModel) : BaseViewRobot<PlayersViewState>() {

    private val fetchEventPlayersSubject = PublishSubject.create<Boolean>()
    private val emitRatingSubject = PublishSubject.create<RateInput>()
    private val leaveEventSubject = PublishSubject.create<Boolean>()

    private val playersView = object : PlayersView {

        override fun render(playersViewState: PlayersViewState) {
            renderedStates.add(playersViewState)
        }

        override fun fetchEventDetailsTriggerEmitter(): Observable<Boolean> = fetchEventPlayersSubject

        override fun ratingEmitter(): Observable<RateInput> = emitRatingSubject

        override fun emitRating(rateInput: RateInput) {
            emitRatingSubject.onNext(rateInput)
        }

        override fun leaveEventEmitter(): Observable<Boolean> = leaveEventSubject
    }

    fun init(eventId: String) {
        playersViewModel.bind(playersView, eventId)
    }

    fun triggerEventPlayersFetching() {
        fetchEventPlayersSubject.onNext(true)
    }

    fun emitRating(rateInput: RateInput) {
        playersView.emitRating(rateInput)
    }

    fun leaveEvent() {
        leaveEventSubject.onNext(true)
    }
}