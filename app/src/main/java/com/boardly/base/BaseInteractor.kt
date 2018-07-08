package com.boardly.base

import com.boardly.constants.USERS_NODE
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

    protected fun getStorageProfilePictureRef(userId: String): StorageReference {
        return firebaseStorage.reference.child(userId)
    }
}