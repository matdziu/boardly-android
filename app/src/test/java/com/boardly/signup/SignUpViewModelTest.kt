package com.boardly.signup

import com.boardly.R
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class SignUpViewModelTest {

    private val signUpInteractor: SignUpInteractor = mock()
    private val signUpViewModel = SignUpViewModel(signUpInteractor)
    private val signUpViewRobot = SignUpViewRobot(signUpViewModel)

    @Test
    fun testWithCorrectInput() {
        whenever(signUpInteractor.createAccount(any(), any()))
                .thenReturn(Observable.just(PartialSignUpViewState.SignUpSuccess()))

        signUpViewRobot.clickCreateAccountButton("test@test.com", "qwerty")

        signUpViewRobot.assertViewStates(SignUpViewState(),
                SignUpViewState(inProgress = true),
                SignUpViewState(signUpSuccess = true))
    }

    @Test
    fun testWithEmptyInput() {
        signUpViewRobot.clickCreateAccountButton(" ", "\n")
        signUpViewRobot.clickCreateAccountButton("test@test", "\n")

        signUpViewRobot.assertViewStates(SignUpViewState(),
                SignUpViewState(emailValid = false, passwordValid = false),
                SignUpViewState(passwordValid = false))
    }

    @Test
    fun testWithErrorFromInteractor() {
        whenever(signUpInteractor.createAccount(any(), any())).thenReturn(
                Observable.just(PartialSignUpViewState.ErrorState(null)))

        signUpViewRobot.clickCreateAccountButton("test@test.com", "qwerty")

        signUpViewRobot.assertViewStates(
                SignUpViewState(),
                SignUpViewState(
                        inProgress = true),
                SignUpViewState(
                        errorMessageId = R.string.generic_error,
                        error = true))
    }
}