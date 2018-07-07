package com.boardly.editprofile

import com.boardly.editprofile.models.InputData
import com.boardly.editprofile.models.ProfileData
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class EditProfileViewModelTest {

    private val outputProfileData = ProfileData("Mateusz", "link/to/pic", 2.0)

    private val editProfileInteractor: EditProfileInteractor = mock {
        on { it.fetchProfileData() } doReturn
                Observable.just(PartialEditProfileViewState.ProfileDataFetched(outputProfileData))
                        .startWith(PartialEditProfileViewState.ProfileDataFetched(outputProfileData, true))
                        .cast(PartialEditProfileViewState::class.java)
        on { it.saveProfileChanges(any()) } doReturn
                Observable.just(PartialEditProfileViewState.SuccessfulUpdateState())
                        .cast(PartialEditProfileViewState::class.java)
    }

    private val editProfileViewModel = EditProfileViewModel(editProfileInteractor)
    private val editProfileViewRobot = EditProfileViewRobot(editProfileViewModel)

    @Test
    fun whenUserNameIsEmptyShowEmptyFieldError() {
        editProfileViewRobot.emitInputData(InputData("  ", null))

        editProfileViewRobot.assertViewStates(
                EditProfileViewState(),
                EditProfileViewState(
                        nameFieldEmpty = true,
                        render = false))
    }

    @Test
    fun whenUserNameIsNotEmptyShowSuccess() {
        editProfileViewRobot.emitInputData(InputData("Mateusz", null))

        editProfileViewRobot.assertViewStates(
                EditProfileViewState(),
                EditProfileViewState(
                        progress = true),
                EditProfileViewState(
                        successfulUpdate = true))
    }

    @Test
    fun testInitialProfileDataFetch() {
        editProfileViewRobot.emitFetchTrigger()

        editProfileViewRobot.assertViewStates(
                EditProfileViewState(),
                EditProfileViewState(
                        progress = true),
                EditProfileViewState(
                        profileData = outputProfileData,
                        render = true),
                EditProfileViewState(
                        profileData = outputProfileData,
                        render = false)
        )
    }
}