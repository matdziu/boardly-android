package com.boardly.editprofile

import com.boardly.editprofile.models.InputData
import io.reactivex.Observable

interface EditProfileView {

    fun render(editProfileViewState: EditProfileViewState)

    fun inputDataEmitter(): Observable<InputData>

    fun fetchProfileDataTriggerEmitter(): Observable<Boolean>
}