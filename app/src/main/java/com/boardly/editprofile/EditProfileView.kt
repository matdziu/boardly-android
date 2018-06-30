package com.boardly.editprofile

import com.boardly.editprofile.models.InputData
import io.reactivex.Observable

interface EditProfileView {

    fun render(editProfileViewState: EditProfileViewState)

    fun emitInputData(): Observable<InputData>

    fun emitFetchProfileDataTrigger(): Observable<Boolean>
}