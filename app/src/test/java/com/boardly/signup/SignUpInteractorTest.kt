package com.boardly.signup

import com.boardly.signup.network.SignUpService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class SignUpInteractorTest {

    @Test
    fun testSuccessfulSignUp() {
        val signUpService: SignUpService = mock {
            on { it.createUserWithEmailAndPassword(any(), any()) } doReturn Observable.just(true)
        }
        val signUpInteractor = SignUpInteractor(signUpService)

        signUpInteractor.createAccount("test@test.pl", "qwerty").test()
                .assertNoErrors()
                .assertValue { it is PartialSignUpViewState.SignUpSuccess }
    }

    @Test
    fun whenErrorOccursInteractorReturnsErrorState() {
        val exception = Exception("testError")
        val signUpService: SignUpService = mock {
            on { it.createUserWithEmailAndPassword(any(), any()) } doReturn Observable.error(exception)
        }
        val signUpInteractor = SignUpInteractor(signUpService)

        signUpInteractor.createAccount("test@test.pl", "qwerty").test()
                .assertValueCount(2)
                .assertValueAt(0, PartialSignUpViewState.ErrorState(exception))
                .assertValueAt(1, PartialSignUpViewState.ErrorState(exception, true))
    }
}