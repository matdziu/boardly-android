package com.boardly.login

sealed class PartialLoginViewState {

    class InProgressState : PartialLoginViewState()

    data class LocalValidation(val emailValid: Boolean = false,
                               val passwordValid: Boolean = false) : PartialLoginViewState()

    data class ErrorState(val exception: Exception?,
                          val dismissToast: Boolean = false) : PartialLoginViewState()

    class LoginSuccess : PartialLoginViewState()
}