package com.boardly.home

import com.boardly.base.BaseInteractor
import com.boardly.common.events.models.Event
import com.boardly.constants.EVENTS_NODE
import com.boardly.constants.PENDING_EVENTS_NODE
import com.boardly.home.models.JoinEventData
import com.boardly.home.models.UserLocation
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryDataEventListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.subjects.PublishSubject

class HomeInteractor : BaseInteractor() {

    fun fetchEvents(userLocation: UserLocation, radius: Double, gameId: String): Observable<PartialHomeViewState> {
        return Observable.zip(fetchUserEventIds(), fetchAllEvents(userLocation, radius, gameId),
                BiFunction<List<String>, List<Event>, PartialHomeViewState>
                { userEventIds, allEvents ->
                    val filteredEventList = allEvents.filter { !userEventIds.contains(it.eventId) }
                    PartialHomeViewState.EventListState(filteredEventList)
                })
    }

    fun joinEvent(joinEventData: JoinEventData): Observable<PartialHomeViewState> {
        val resultSubject = PublishSubject.create<PartialHomeViewState>()

        Tasks.whenAllComplete(
                getUserPendingEventsNodeRef(currentUserId)
                        .push()
                        .setValue(joinEventData.eventId),
                getSingleEventNode(joinEventData.eventId)
                        .child(PENDING_EVENTS_NODE)
                        .child(currentUserId)
                        .setValue(joinEventData.helloText))
                .addOnSuccessListener { resultSubject.onNext(PartialHomeViewState.JoinRequestSent()) }

        return resultSubject
    }

    private fun fetchUserEventIds(): Observable<List<String>> {
        return Observable.zip(pendingEventIdsList(), acceptedEventIdsList(), createdEventIdsList(),
                Function3<List<String>, List<String>, List<String>, List<String>>
                { pending, accepted, created -> pending + accepted + created })
    }

    private fun fetchAllEvents(userLocation: UserLocation, radius: Double, gameId: String): Observable<List<Event>> {
        val resultSubject = PublishSubject.create<List<Event>>()
        val fetchedEventsList = arrayListOf<Event>()

        getGeoFire(EVENTS_NODE).queryAtLocation(GeoLocation(userLocation.latitude, userLocation.longitude), radius)
                .addGeoQueryDataEventListener(object : GeoQueryDataEventListener {
                    override fun onDataEntered(dataSnapshot: DataSnapshot, location: GeoLocation) {
                        dataSnapshot.getValue(Event::class.java)?.let {
                            if (gameId.isEmpty() || it.gameId == gameId) {
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
}