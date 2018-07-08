package com.boardly.addevent

import com.boardly.base.BaseInteractor
import com.boardly.retrofit.gamesearch.GameSearchService
import com.boardly.retrofit.gamesearch.models.DetailsResponse
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*
import javax.inject.Inject


class AddEventInteractor @Inject constructor(private val gameSearchService: GameSearchService) : BaseInteractor() {

    fun fetchGameDetails(gameId: String): Observable<PartialAddEventViewState> {
        return gameSearchService.details(gameId)
                .onErrorReturn { DetailsResponse() }
                .map { PartialAddEventViewState.GameDetailsFetched(it.game) }
    }

    fun addEvent(inputData: InputData): Observable<PartialAddEventViewState> {
        val resultSubject = PublishSubject.create<PartialAddEventViewState>()

        val eventKey = UUID.randomUUID().toString()
        val geoLocation = GeoLocation(inputData.placeLatitude, inputData.placeLongitude)

        getEventsNode()
                .child(eventKey)
                .setValue(inputData)
                .continueWithTask { setGeoLocationTask(eventKey, geoLocation) }
                .addOnCompleteListener { resultSubject.onNext(PartialAddEventViewState.SuccessState()) }

        return resultSubject
    }

    private fun setGeoLocationTask(eventKey: String, geoLocation: GeoLocation): Task<String> {
        val geoSource = TaskCompletionSource<String>()
        val geoTask = geoSource.task
        getGeoFire().setLocation(eventKey, geoLocation) { key, _ ->
            geoSource.setResult(key)
        }
        return geoTask
    }
}