package com.boardly.home.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.events.models.Event
import com.boardly.common.location.UserLocation
import com.boardly.constants.EVENTS_NODE
import com.boardly.constants.NOTIFICATION_TOKEN_CHILD
import com.boardly.home.models.JoinEventData
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryDataEventListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.subjects.PublishSubject

class HomeServiceImpl : HomeService, BaseServiceImpl() {

    override fun sendClientNotificationToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            getUserNodeRef(currentUserId).child(NOTIFICATION_TOKEN_CHILD).setValue(it.result.token)
        }
    }

    override fun fetchUserEventIds(): Observable<List<String>> {
        return Observable.zip(pendingEventIdsList(), acceptedEventIdsList(), createdEventIdsList(),
                Function3<List<String>, List<String>, List<String>, List<String>>
                { pending, accepted, created -> pending + accepted + created })
    }

    override fun fetchAllEvents(userLocation: UserLocation, radius: Double, gameId: String): Observable<List<Event>> {
        val resultSubject = PublishSubject.create<List<Event>>()
        val fetchedEventsList = arrayListOf<Event>()

        getGeoFire(EVENTS_NODE).queryAtLocation(GeoLocation(userLocation.latitude, userLocation.longitude), radius)
                .addGeoQueryDataEventListener(object : GeoQueryDataEventListener {
                    override fun onDataEntered(dataSnapshot: DataSnapshot, location: GeoLocation) {
                        dataSnapshot.getValue(Event::class.java)?.let {
                            if (gameId.isEmpty() ||
                                    it.gameId == gameId ||
                                    it.gameId2 == gameId ||
                                    it.gameId3 == gameId) {
                                it.eventId = dataSnapshot.key.orEmpty()
                                fetchedEventsList.add(it)
                            }
                        }
                    }

                    override fun onGeoQueryReady() {
                        resultSubject.onNext(fetchedEventsList)
                    }

                    override fun onDataExited(dataSnapshot: DataSnapshot) {
                        // unused
                    }

                    override fun onDataChanged(dataSnapshot: DataSnapshot, location: GeoLocation) {
                        // unused
                    }

                    override fun onDataMoved(dataSnapshot: DataSnapshot, location: GeoLocation) {
                        // unused
                    }

                    override fun onGeoQueryError(error: DatabaseError) {
                        // unused
                    }
                })

        return resultSubject
    }

    override fun sendJoinRequest(joinEventData: JoinEventData): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        Tasks.whenAllComplete(
                getUserPendingEventsNodeRef(currentUserId)
                        .push()
                        .setValue(joinEventData.eventId),
                getPendingPlayersNode(joinEventData.eventId)
                        .child(currentUserId)
                        .setValue(joinEventData.helloText))
                .addOnSuccessListener { resultSubject.onNext(true) }
        return resultSubject
    }
}