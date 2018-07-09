package com.boardly.base

import com.boardly.constants.EVENTS_NODE
import com.boardly.constants.USERS_NODE
import com.firebase.geofire.GeoFire
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

open class BaseInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    protected val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    protected val currentUserId = firebaseAuth.currentUser?.uid ?: ""

    protected fun getUserNodeRef(userId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$userId")
    }

    protected fun getEventsNode(): DatabaseReference {
        return firebaseDatabase.getReference(EVENTS_NODE)
    }

    protected fun getStorageProfilePictureRef(userId: String): StorageReference {
        return firebaseStorage.reference.child(userId)
    }

    protected fun getGeoFire(childPath: String): GeoFire {
        return GeoFire(firebaseDatabase.getReference(childPath))
    }
}