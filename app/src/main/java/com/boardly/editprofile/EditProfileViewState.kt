package com.boardly.editprofile

import com.boardly.editprofile.models.ProfileData

data class EditProfileViewState(val progress: Boolean = false,
                                val successfulUpdate: Boolean = false,
                                val nameFieldEmpty: Boolean = false,
                                val profileData: ProfileData = ProfileData())