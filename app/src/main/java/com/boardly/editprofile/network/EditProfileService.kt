package com.boardly.editprofile.network

import com.boardly.editprofile.models.InputData
import com.boardly.editprofile.models.ProfileData
import io.reactivex.Observable

interface EditProfileService {

    fun getProfileData(): Observable<ProfileData>

    fun saveProfileChanges(inputData: InputData): Observable<Boolean>
}