package com.boardly.discover.network

import com.boardly.base.BaseServiceImpl
import com.boardly.common.location.UserLocation
import com.boardly.constants.PLACES_NODE
import com.boardly.discover.models.Place
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryDataEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class DiscoverServiceImpl : DiscoverService, BaseServiceImpl() {

    override fun fetchPlacesList(userLocation: UserLocation, radius: Double): Observable<List<Place>> {
        val resultSubject = PublishSubject.create<List<Place>>()
        val fetchedPlacesList = arrayListOf<Place>()

        getGeoFire(PLACES_NODE).queryAtLocation(GeoLocation(userLocation.latitude, userLocation.longitude), radius)
                .addGeoQueryDataEventListener(object : GeoQueryDataEventListener {
                    override fun onDataEntered(dataSnapshot: DataSnapshot, location: GeoLocation) {
                        dataSnapshot.getValue(Place::class.java)?.let {
                            it.id = dataSnapshot.key.orEmpty()
                            fetchedPlacesList.add(it)
                        }
                    }

                    override fun onGeoQueryReady() {
                        fetchedPlacesList.shuffle()
                        resultSubject.onNext(fetchedPlacesList)
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