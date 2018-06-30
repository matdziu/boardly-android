package com.boardly.editprofile

data class EditProfileViewState(val progress: Boolean = false,
                                val success: Boolean = false,
                                val nameFieldEmpty: Boolean = false)