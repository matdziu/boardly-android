package com.boardly.editprofile

import com.boardly.editprofile.models.InputData
import com.boardly.editprofile.models.ProfileData
import com.boardly.editprofile.network.EditProfileService
import io.reactivex.Observable
import javax.inject.Inject

class EditProfileInteractor @Inject constructor(private val editProfileService: EditProfileService) {

    fun fetchProfileData(): Observable<PartialEditProfileViewState> {
        return editProfileService.getProfileData()
                .flatMap { emitProfileDataFetchedState(it) }
    }

    private fun emitProfileDataFetchedState(profileData: ProfileData)
            : Observable<PartialEditProfileViewState.ProfileDataFetched> {
        val profileDataFetchedState = PartialEditProfileViewState.ProfileDataFetched(profileData)
        return Observable.just(profileDataFetchedState).startWith(profileDataFetchedState.copy(render = true))
    }

    fun saveProfileChanges(inputData: InputData): Observable<PartialEditProfileViewState> {
        return editProfileService.saveProfileChanges(inputData)
                .filter { it }
                .map { PartialEditProfileViewState.SuccessfulUpdateState() }
    }
}