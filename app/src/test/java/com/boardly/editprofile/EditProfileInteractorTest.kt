package com.boardly.editprofile

import com.boardly.editprofile.models.InputData
import com.boardly.editprofile.models.ProfileData
import com.boardly.editprofile.network.EditProfileService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class EditProfileInteractorTest {

    private val testProfileData = ProfileData("Mateusz", "link/to/pick", 1.0)
    private val editProfileService: EditProfileService = mock {
        on { it.getProfileData() } doReturn Observable.just(testProfileData)
        on { it.saveProfileChanges(any()) } doReturn Observable.just(true)
    }
    private val editProfileInteractor = EditProfileInteractor(editProfileService)

    @Test
    fun testSuccessfulProfileFetching() {
        editProfileInteractor.fetchProfileData().test()
                .assertValueAt(0, PartialEditProfileViewState.ProfileDataFetched(testProfileData, true))
                .assertValueAt(1, PartialEditProfileViewState.ProfileDataFetched(testProfileData))
    }

    @Test
    fun testSuccessfulProfileChangesSaving() {
        editProfileInteractor.saveProfileChanges(InputData("test", null)).test()
                .assertValue { it is PartialEditProfileViewState.SuccessfulUpdateState }
    }
}