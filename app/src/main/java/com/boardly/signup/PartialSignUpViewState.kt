package com.boardly.signup

import com.boardly.R
import com.boardly.constants.ERROR_EMAIL_ALREADY_IN_USE
import com.boardly.constants.ERROR_INVALID_EMAIL
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import java.lang.Exception

sealed class PartialSignUpViewState {

    abstract fun reduce(previousState: SignUpViewState): SignUpViewState

    class InProgressState : PartialSignUpViewState() {
        override fun reduce(previousState: SignUpViewState) = SignUpViewState(true)
    }

    data class LocalValidation(private val emailValid: Boolean = false,
                               private val passwordValid: Boolean = false) : PartialSignUpViewState() {
        override fun reduce(previousState: SignUpViewState) = SignUpViewState(
                emailValid = emailValid,
                passwordValid = passwordValid)
    }

    data class ErrorState(private val exception: Exception?,
                          val dismissToast: Boolean = false) : PartialSignUpViewState() {
        override fun reduce(previousState: SignUpViewState) = SignUpViewState(
                errorMessageId = getErrorMessageId(exception),
                error = true,
                dismissToast = dismissToast)

        private fun getErrorMessageId(exception: Exception?): Int {
            return when (exception) {
                is FirebaseNetworkException -> R.string.no_internet_error
                is FirebaseAuthException -> getErrorMessageId(exception)
                else -> R.string.generic_error
            }
        }

        private fun getErrorMessageId(exception: FirebaseAuthException): Int {
            return when (exception.errorCode) {
                ERROR_EMAIL_ALREADY_IN_USE -> R.string.already_used_email_error
                ERROR_INVALID_EMAIL -> R.string.invalid_email_error
                else -> R.string.generic_error
            }
        }
    }

    class SignUpSuccess : PartialSignUpViewState() {
        override fun reduce(previousState: SignUpViewState) = SignUpViewState(signUpSuccess = true)
    }
}