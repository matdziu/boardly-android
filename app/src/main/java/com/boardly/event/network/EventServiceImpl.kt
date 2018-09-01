package com.boardly.event.network

import com.boardly.base.BaseServiceImpl
import com.boardly.constants.EVENTS_NODE
import com.boardly.event.InputData
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

class EventServiceImpl : EventService, BaseServiceImpl() {

    override fun editEvent(inputData: InputData): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        val geoLocation = GeoLocation(inputData.placeLatitude, inputData.placeLongitude)
        setGeoLocationTask(inputData.eventId, geoLocation)
                .continueWithTask { getSingleEventNode(inputData.eventId).updateChildren(inputData.toMap()) }
                .addOnCompleteListener { resultSubject.onNext(true) }

        return resultSubject
    }

    override fun deleteEvent(eventId: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        Tasks.whenAllComplete(
                removeEventChat(eventId),
                removeEventInfo(eventId),
                removeEventFromPending(eventId),
                removeEventFromAccepted(eventId),
                removeEventFromCreated(eventId))
                .addOnSuccessListener { resultSubject.onNext(true) }

        return resultSubject
    }

    private fun removeEventChat(eventId: String): Task<Void> {
        return getChatNodeReference(eventId).removeValue()
    }

    private fun removeEventInfo(eventId: String): Task<Void> {
        return getSingleEventNode(eventId).removeValue()
    }

    private fun removeEventFromPending(eventId: String): Task<Void> {
        return getKeysTask(getPendingPlayersNode(eventId))
                .continueWithTask { removeForAllWithValue(it.result, { getUserPendingEventsNodeRef(it) }, eventId) }
                .continueWithTask { getPendingPlayersNode(eventId).removeValue() }
    }

    private fun removeEventFromAccepted(eventId: String): Task<Void> {
        return getKeysTask(getAcceptedPlayersNode(eventId))
                .continueWithTask { removeForAllWithValue(it.result, { getUserAcceptedEventsNodeRef(it) }, eventId) }
                .continueWithTask { getAcceptedPlayersNode(eventId).removeValue() }
    }

    private fun removeEventFromCreated(eventId: String): Task<Any> {
        return removeWithValue(eventId, getUserCreatedEventsNodeRef(currentUserId))
    }

    private fun removeForAllWithValue(childrenList: List<String>, databaseReferenceFunction: (String) -> DatabaseReference, value: String)
            : Task<List<Task<*>>> {
        val removeTasksList = arrayListOf<Task<Any>>()
        for (child in childrenList) {
            removeTasksList.add(removeWithValue(value, databaseReferenceFunction(child)))
        }
        return Tasks.whenAllComplete(removeTasksList)
    }

    private fun removeWithValue(value: String, databaseReference: DatabaseReference): Task<Any> {
        val removeSource = TaskCompletionSource<Any>()
        val removeTask = removeSource.task
        databaseReference.orderByValue().equalTo(value).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChildren()) removeSource.setResult(Any())
                else for (childSnapshot in dataSnapshot.children) {
                    childSnapshot.ref.removeValue().addOnSuccessListener { removeSource.setResult(Any()) }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                removeSource.setException(databaseError.toException())
            }
        })
        return removeTask
    }

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