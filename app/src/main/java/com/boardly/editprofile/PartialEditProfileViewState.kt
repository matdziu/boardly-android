package com.boardly.editprofile

import com.boardly.editprofile.models.ProfileData

sealed class PartialEditProfileViewState {

    abstract fun reduce(previousState: EditProfileViewState): EditProfileViewState

    class ProgressState : PartialEditProfileViewState() {
        override fun reduce(previousState: EditProfileViewState) = EditProfileViewState(progress = true)
    }

    class SuccessfulUpdateState : PartialEditProfileViewState() {
        override fun reduce(previousState: EditProfileViewState) = EditProfileViewState(successfulUpdate = true)
    }

    class NameFieldEmptyState : PartialEditProfileViewState() {
        override fun reduce(previousState: EditProfileViewState) = EditProfileViewState(nameFieldEmpty = true)
    }

    class ProfileDataFetched(private val profileData: ProfileData) : PartialEditProfileViewState() {
        override fun reduce(previousState: EditProfileViewState): EditProfileViewState {
            return previousState.copy(profileData = profileData)
        }
    }
}