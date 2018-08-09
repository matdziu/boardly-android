package com.boardly.editprofile.network

import android.net.Uri
import com.boardly.base.BaseServiceImpl
import com.boardly.constants.NAME_CHILD
import com.boardly.constants.PROFILE_PICTURE_CHILD
import com.boardly.editprofile.models.InputData
import com.boardly.editprofile.models.ProfileData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.UploadTask
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.io.File

class EditProfileServiceImpl : EditProfileService, BaseServiceImpl() {

    override fun getProfileData(): Observable<ProfileData> {
        val resultSubject = PublishSubject.create<ProfileData>()

        getUserNodeRef(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val profileData = dataSnapshot.getValue(ProfileData::class.java)

                if (profileData != null) {
                    resultSubject.onNext(profileData)
                } else {
                    resultSubject.onNext(ProfileData())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // unused
            }
        })

        return resultSubject
    }

    override fun saveProfileChanges(inputData: InputData): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()

        with(inputData) {
            if (profilePictureFile != null) {
                uploadProfilePicture(profilePictureFile)
                        .continueWithTask { getStorageProfilePictureRef(currentUserId).downloadUrl }
                        .continueWithTask { saveProfilePictureUrl(it.result.toString()) }
                        .continueWithTask { saveUserName(name) }
                        .addOnSuccessListener { resultSubject.onNext(true) }
            } else {
                saveUserName(name)
                        .addOnSuccessListener { resultSubject.onNext(true) }
            }
        }

        return resultSubject
    }

    private fun uploadProfilePicture(profilePicture: File): UploadTask {
        val profilePictureUri = Uri.fromFile(profilePicture)
        return getStorageProfilePictureRef(currentUserId).putFile(profilePictureUri)
    }

    private fun saveUserName(name: String): Task<Void> {
        return getUserNodeRef(currentUserId).child(NAME_CHILD).setValue(name)
    }

    private fun saveProfilePictureUrl(pictureUrl: String): Task<Void> {
        return getUserNodeRef(currentUserId).child(PROFILE_PICTURE_CHILD).setValue(pictureUrl)
    }
}