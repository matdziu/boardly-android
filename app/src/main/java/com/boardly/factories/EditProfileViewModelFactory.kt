package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.editprofile.EditProfileInteractor
import com.boardly.editprofile.EditProfileViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class EditProfileViewModelFactory @Inject constructor(private val editProfileInteractor: EditProfileInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditProfileViewModel(editProfileInteractor) as T
    }
}