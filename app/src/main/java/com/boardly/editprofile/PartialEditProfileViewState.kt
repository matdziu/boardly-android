package com.boardly.editprofile

sealed class PartialEditProfileViewState {

    abstract fun reduce(previousState: EditProfileViewState): EditProfileViewState

    class ProgressState : PartialEditProfileViewState() {
        override fun reduce(previousState: EditProfileViewState) = EditProfileViewState(progress = true)
    }

    class SuccessState : PartialEditProfileViewState() {
        override fun reduce(previousState: EditProfileViewState) = EditProfileViewState(success = true)
    }

    class NameFieldEmptyState : PartialEditProfileViewState() {
        override fun reduce(previousState: EditProfileViewState) = EditProfileViewState(nameFieldEmpty = true)
    }
}