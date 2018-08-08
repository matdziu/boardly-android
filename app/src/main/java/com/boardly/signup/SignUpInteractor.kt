package com.boardly.signup

import com.boardly.signup.network.SignUpService
import io.reactivex.Observable
import javax.inject.Inject

class SignUpInteractor @Inject constructor(private val signUpService: SignUpService) {

    fun createAccount(email: String, password: String): Observable<PartialSignUpViewState> {
        return signUpService.createUserWithEmailAndPassword(email, password)
                .filter { it }
                .map { PartialSignUpViewState.SignUpSuccess() }
                .cast(PartialSignUpViewState::class.java)
                .onErrorResumeNext { throwable: Throwable -> emitErrorState(throwable as Exception) }
    }

    private fun emitErrorState(exception: Exception): Observable<PartialSignUpViewState.ErrorState> {
        val errorState = PartialSignUpViewState.ErrorState(exception)
        return Observable.just(errorState.copy(dismissToast = true)).startWith(errorState)
    }
}