package com.boardly.editprofile

import io.reactivex.Observable

class EditProfileInteractor {

    fun saveProfileChanges(inputData: InputData): Observable<PartialEditProfileViewState> {
        return Observable.just(PartialEditProfileViewState.SuccessState())
    }
}