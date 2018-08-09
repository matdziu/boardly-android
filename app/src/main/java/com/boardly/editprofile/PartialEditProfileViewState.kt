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
        override fun reduce(previousState: EditProfileViewState) = EditProfileViewState(
                nameFieldEmpty = true,
                render = false)
    }

    data class ProfileDataFetched(private val profileData: ProfileData = ProfileData(),
                                  private val render: Boolean = false) : PartialEditProfileViewState() {
        override fun reduce(previousState: EditProfileViewState): EditProfileViewState {
            return previousState.copy(
                    profileData = profileData,
                    render = render,
                    progress = false)
        }
    }
}