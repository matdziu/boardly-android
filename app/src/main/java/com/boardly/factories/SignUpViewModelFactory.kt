package com.boardly.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.boardly.signup.SignUpInteractor
import com.boardly.signup.SignUpViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class SignUpViewModelFactory @Inject constructor(private val signUpInteractor: SignUpInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignUpViewModel(signUpInteractor) as T
    }
}