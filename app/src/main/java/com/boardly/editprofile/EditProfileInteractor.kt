package com.boardly.editprofile

import android.net.Uri
import com.boardly.base.BaseInteractor
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

class EditProfileInteractor : BaseInteractor() {

    fun fetchProfileData(): Observable<PartialEditProfileViewState> {
        val resultSubject = PublishSubject.create<PartialEditProfileViewState>()

        getUserNodeRef(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(ProfileData::class.java)?.let {
                    resultSubject.onNext(PartialEditProfileViewState.ProfileDataFetched(it))
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                // unused
            }
        })

        return resultSubject
    }

    fun saveProfileChanges(inputData: InputData): Observable<PartialEditProfileViewState> {
        val resultSubject = PublishSubject.create<PartialEditProfileViewState>()

        with(inputData) {
            if (profilePictureFile != null) {
                uploadProfilePicture(profilePictureFile)
                        .continueWithTask { getStorageProfilePictureRef(currentUserId).downloadUrl }
                        .continueWithTask { saveProfilePictureUrl(it.result.toString()) }
                        .continueWithTask { saveUserName(name) }
                        .addOnSuccessListener { resultSubject.onNext(PartialEditProfileViewState.SuccessfulUpdateState()) }
            } else {
                saveUserName(name)
                        .addOnSuccessListener { resultSubject.onNext(PartialEditProfileViewState.SuccessfulUpdateState()) }
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

