package com.boardly.manageplace.network

import android.net.Uri
import com.boardly.base.BaseServiceImpl
import com.boardly.constants.MANAGED_PLACE_CHILD
import com.boardly.constants.PLACES_NODE
import com.boardly.constants.PLACE_IMAGE_CHILD
import com.boardly.discover.models.Place
import com.boardly.manageplace.models.PlaceInputData
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.UploadTask
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.io.File

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

    override fun savePlaceData(placeId: String, placeInputData: PlaceInputData): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        if (placeId.isNotEmpty()) {
            placeInputData.place.adminId = currentUserId
            val imageFile = placeInputData.imageFile
            if (imageFile != null) {
                savePlaceDataTask(placeId, placeInputData)
                        .continueWithTask { uploadPlacePicture(placeId, imageFile) }
                        .continueWithTask { getStoragePlacePictureRef(placeId).downloadUrl }
                        .continueWithTask { savePlacePictureUrl(placeId, it.result.toString()) }
                        .addOnSuccessListener { resultSubject.onNext(true) }
            } else {
                savePlaceDataTask(placeId, placeInputData)
                        .addOnSuccessListener { resultSubject.onNext(true) }
            }
        }
        return resultSubject
    }

    private fun savePlaceDataTask(placeId: String, placeInputData: PlaceInputData): Task<Void> {
        return setGeoLocationTask(placeId, GeoLocation(
                placeInputData.place.placeLatitude,
                placeInputData.place.placeLongitude))
                .continueWithTask {
                    getSinglePlaceRef(placeId)
                            .updateChildren(placeInputData.place.toMap())
                }
    }

    private fun uploadPlacePicture(placeId: String, placePicture: File): UploadTask {
        val placePictureUri = Uri.fromFile(placePicture)
        return getStoragePlacePictureRef(placeId).putFile(placePictureUri)
    }

    private fun savePlacePictureUrl(placeId: String, pictureUrl: String): Task<Void> {
        return getSinglePlaceRef(placeId).child(PLACE_IMAGE_CHILD).setValue(pictureUrl)
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