package com.boardly.addevent.network

import com.boardly.addevent.InputData
import com.boardly.base.BaseServiceImpl
import com.boardly.constants.EVENTS_NODE
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

class AddEventServiceImpl : AddEventService, BaseServiceImpl() {

    override fun addEvent(inputData: InputData): Observable<Boolean> {
        inputData.adminId = currentUserId
        val resultSubject = PublishSubject.create<Boolean>()

        val eventKey = UUID.randomUUID().toString()
        val geoLocation = GeoLocation(inputData.placeLatitude, inputData.placeLongitude)

        setGeoLocationTask(eventKey, geoLocation)
                .continueWithTask {
                    Tasks.whenAllComplete(
                            getSingleEventNode(eventKey)
                                    .updateChildren(inputData.toMap()),
                            getUserCreatedEventsNodeRef(currentUserId)
                                    .push()
                                    .setValue(eventKey))
                }
                .addOnCompleteListener { resultSubject.onNext(true) }

        return resultSubject
    }

    private fun setGeoLocationTask(eventKey: String, geoLocation: GeoLocation): Task<String> {
        val geoSource = TaskCompletionSource<String>()
        val geoTask = geoSource.task
        getGeoFire(EVENTS_NODE).setLocation(eventKey, geoLocation) { key, _ ->
            geoSource.setResult(key)
        }
        return geoTask
    }
}