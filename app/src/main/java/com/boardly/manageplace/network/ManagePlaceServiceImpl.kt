package com.boardly.manageplace.network

import com.boardly.base.BaseServiceImpl
import com.boardly.constants.MANAGED_PLACE_CHILD
import com.boardly.constants.PLACES_NODE
import com.boardly.discover.models.Place
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ManagePlaceServiceImpl : ManagePlaceService, BaseServiceImpl() {

    override fun fetchPlaceData(placeId: String): Observable<Place> {
        val resultSubject = PublishSubject.create<Place>()
        getSinglePlaceRef(placeId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val placeData = dataSnapshot.getValue(Place::class.java) ?: Place()
                resultSubject.onNext(placeData)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // unused
            }
        })
        return resultSubject
    }

    override fun savePlaceData(placeId: String, place: Place): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        if (placeId.isNotEmpty()) {
            place.adminId = currentUserId
            setGeoLocationTask(placeId, GeoLocation(place.placeLatitude, place.placeLongitude))
                    .continueWithTask {
                        getSinglePlaceRef(placeId)
                                .updateChildren(place.toMap())
                    }
                    .addOnSuccessListener { resultSubject.onNext(true) }
        }
        return resultSubject
    }

    private fun setGeoLocationTask(placeId: String, geoLocation: GeoLocation): Task<String> {
        val geoSource = TaskCompletionSource<String>()
        val geoTask = geoSource.task
        getGeoFire(PLACES_NODE).setLocation(placeId, geoLocation) { key, _ ->
            geoSource.setResult(key)
        }
        return geoTask
    }

    override fun fetchManagedPlaceId(): Observable<String> {
        val resultSubject = PublishSubject.create<String>()
        getUserNodeRef(currentUserId)
                .child(MANAGED_PLACE_CHILD)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val managedPlaceId = dataSnapshot.getValue(String::class.java) ?: ""
                        resultSubject.onNext(managedPlaceId)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        resultSubject.onNext("")
                    }
                })
        return resultSubject
    }
}