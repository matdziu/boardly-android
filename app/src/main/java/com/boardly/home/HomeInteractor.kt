package com.boardly.home

import com.boardly.base.BaseInteractor
import com.boardly.constants.EVENTS_NODE
import com.boardly.home.models.Event
import com.boardly.home.models.UserLocation
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryDataEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class HomeInteractor : BaseInteractor() {

    fun fetchEvents(userLocation: UserLocation, radius: Double, gameId: String): Observable<PartialHomeViewState> {
        val resultSubject = PublishSubject.create<PartialHomeViewState>()
        val fetchedEventsList = arrayListOf<Event>()

        getGeoFire(EVENTS_NODE).queryAtLocation(GeoLocation(userLocation.latitude, userLocation.longitude), radius)
                .addGeoQueryDataEventListener(object : GeoQueryDataEventListener {
                    override fun onDataEntered(dataSnapshot: DataSnapshot, location: GeoLocation) {
                        dataSnapshot.getValue(Event::class.java)?.let {
                            if (gameId.isEmpty() || it.gameId == gameId) {
                                it.eventId = dataSnapshot.key ?: ""
                                fetchedEventsList.add(it)
                            }
                        }
                    }

                    override fun onGeoQueryReady() {
                        resultSubject.onNext(PartialHomeViewState.EventsFetchedState(fetchedEventsList))
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