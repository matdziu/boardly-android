package com.boardly.eventdetails.players

import com.boardly.base.BaseViewRobot
import com.boardly.base.rating.models.RateInput
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PlayersViewRobot(playersViewModel: PlayersViewModel) : BaseViewRobot<PlayersViewState>() {

    private val fetchEventPlayersSubject = PublishSubject.create<String>()
    private val emitRatingSubject = PublishSubject.create<RateInput>()

    private val playersView = object : PlayersView {
        override fun render(playersViewState: PlayersViewState) {
            renderedStates.add(playersViewState)
        }

        override fun fetchEventPlayersTriggerEmitter(): Observable<String> = fetchEventPlayersSubject

        override fun ratingEmitter(): Observable<RateInput> = emitRatingSubject

        override fun emitRating(rateInput: RateInput) {
            emitRatingSubject.onNext(rateInput)
        }
    }

    init {
        playersViewModel.bind(playersView)
    }

    fun triggerEventPlayersFetching(eventId: String) {
        fetchEventPlayersSubject.onNext(eventId)
    }

    fun emitRating(rateInput: RateInput) {
        playersView.emitRating(rateInput)
    }
}