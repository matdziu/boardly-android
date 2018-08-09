package com.boardly.login

import com.boardly.R
import com.boardly.constants.ERROR_INVALID_EMAIL
import com.boardly.constants.ERROR_USER_NOT_FOUND
import com.boardly.constants.ERROR_WRONG_PASSWORD
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException

sealed class PartialLoginViewState {

    abstract fun reduce(previousState: LoginViewState): LoginViewState

    class InProgressState : PartialLoginViewState() {
        override fun reduce(previousState: LoginViewState) = LoginViewState(true)
    }

    data class LocalValidation(val emailValid: Boolean = false,
                               val passwordValid: Boolean = false) : PartialLoginViewState() {
        override fun reduce(previousState: LoginViewState) = LoginViewState(
                emailValid = emailValid,
                passwordValid = passwordValid)
    }

    data class ErrorState(private val exception: Exception?,
                          val dismissToast: Boolean = false) : PartialLoginViewState() {
        override fun reduce(previousState: LoginViewState) = LoginViewState(
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
                ERROR_INVALID_EMAIL -> R.string.invalid_email_error
                ERROR_WRONG_PASSWORD -> R.string.wrong_password_error
                ERROR_USER_NOT_FOUND -> R.string.user_not_found_error
                else -> R.string.generic_error
            }
        }
    }

    data class LoginSuccess(private val isProfileFilled: Boolean = true) : PartialLoginViewState() {
        override fun reduce(previousState: LoginViewState) = LoginViewState(
                inProgress = true,
                loginSuccess = true,
                notLoggedIn = false,
                isProfileFilled = isProfileFilled)
    }

    class NotLoggedIn : PartialLoginViewState() {
        override fun reduce(previousState: LoginViewState) = previousState.copy(
                inProgress = false,
                loginSuccess = false,
                notLoggedIn = true)
    }
}