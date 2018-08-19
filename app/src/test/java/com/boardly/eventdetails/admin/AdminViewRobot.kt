package com.boardly.eventdetails.admin

import com.boardly.base.BaseViewRobot
import com.boardly.base.rating.models.RateInput
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AdminViewRobot(adminViewModel: AdminViewModel) : BaseViewRobot<AdminViewState>() {

    private val fetchEventPlayersSubject = PublishSubject.create<Boolean>()
    private val kickPlayerSubject = PublishSubject.create<String>()
    private val acceptPlayerSubject = PublishSubject.create<String>()
    private val emitRatingSubject = PublishSubject.create<RateInput>()

    private val adminView = object : AdminView {
        override fun render(adminViewState: AdminViewState) {
            renderedStates.add(adminViewState)
        }

        override fun fetchEventPlayersTriggerEmitter(): Observable<Boolean> = fetchEventPlayersSubject

        override fun kickPlayerEmitter(): Observable<String> = kickPlayerSubject

        override fun acceptPlayerEmitter(): Observable<String> = acceptPlayerSubject

        override fun ratingEmitter(): Observable<RateInput> = emitRatingSubject

        override fun emitRating(rateInput: RateInput) {
            emitRatingSubject.onNext(rateInput)
        }
    }

    init {
        adminViewModel.bind(adminView, "testEventId")
    }

    fun triggerEventPlayersFetching() {
        fetchEventPlayersSubject.onNext(true)
    }

    fun kickPlayer(playerId: String) {
        kickPlayerSubject.onNext(playerId)
    }

    fun acceptPlayer(playerId: String) {
        acceptPlayerSubject.onNext(playerId)
    }

    fun emitRating(rateInput: RateInput) {
        adminView.emitRating(rateInput)
    }
}