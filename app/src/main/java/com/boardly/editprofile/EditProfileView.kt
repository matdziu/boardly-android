package com.boardly.editprofile

import io.reactivex.Observable

interface EditProfileView {

    fun render(editProfileViewState: EditProfileViewState)

    fun emitInputData(): Observable<InputData>
}