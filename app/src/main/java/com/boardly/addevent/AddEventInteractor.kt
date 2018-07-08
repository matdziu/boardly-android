package com.boardly.addevent

import com.boardly.base.BaseInteractor
import com.boardly.retrofit.gamesearch.GameSearchService
import com.boardly.retrofit.gamesearch.models.DetailsResponse
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class AddEventInteractor @Inject constructor(private val gameSearchService: GameSearchService) : BaseInteractor() {

    fun fetchGameDetails(gameId: String): Observable<PartialAddEventViewState> {
        return gameSearchService.details(gameId)
                .onErrorReturn { DetailsResponse() }
                .map { PartialAddEventViewState.GameDetailsFetched(it.game) }
    }

    fun addEvent(inputData: InputData): Observable<PartialAddEventViewState> {
        val resultSubject = PublishSubject.create<PartialAddEventViewState>()

        getEventsNode()
                .push()
                .setValue(inputData)
                .addOnCompleteListener { resultSubject.onNext(PartialAddEventViewState.SuccessState()) }

        return resultSubject
    }
}