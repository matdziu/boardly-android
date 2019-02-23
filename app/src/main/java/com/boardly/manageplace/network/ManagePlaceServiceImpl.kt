package com.boardly.manageplace.network

import com.boardly.base.BaseServiceImpl
import com.boardly.constants.MANAGED_PLACE_CHILD
import com.boardly.discover.models.Place
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.UUID

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
        if (placeId.isEmpty()) {
            val placeKey = getPlacesRef().push().key ?: UUID.randomUUID().toString()
            Tasks.whenAllComplete(
                    getSinglePlaceRef(placeKey)
                            .push()
                            .setValue(place),
                    getUserNodeRef(currentUserId)
                            .child(MANAGED_PLACE_CHILD)
                            .setValue(placeKey))
                    .addOnSuccessListener { resultSubject.onNext(true) }
        } else {
            getSinglePlaceRef(placeId)
                    .setValue(place)
                    .addOnSuccessListener { resultSubject.onNext(true) }
        }
        return resultSubject
    }

    override fun fetchManagedPlaceId(): Observable<String> {
        val resultSubject = PublishSubject.create<String>()
        getUserNodeRef(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
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