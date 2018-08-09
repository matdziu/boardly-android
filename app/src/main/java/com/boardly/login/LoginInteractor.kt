package com.boardly.login

import com.boardly.login.network.LoginService
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.Observable
import javax.inject.Inject


class LoginInteractor @Inject constructor(private val loginService: LoginService) {

    fun login(email: String, password: String): Observable<PartialLoginViewState> {
        return loginService.login(email, password)
                .map { PartialLoginViewState.LoginSuccess(it) }
                .cast(PartialLoginViewState::class.java)
                .onErrorResumeNext { throwable: Throwable -> emitErrorState(throwable as Exception) }
    }

    fun isLoggedIn(): Observable<PartialLoginViewState> {
        return loginService.isLoggedIn()
                .map {
                    if (it.isLoggedIn) PartialLoginViewState.LoginSuccess(it.isProfileFilled)
                    else PartialLoginViewState.NotLoggedIn()
                }
                .cast(PartialLoginViewState::class.java)
                .onErrorResumeNext { throwable: Throwable -> emitErrorState(throwable as Exception) }
    }

    fun login(googleAccount: GoogleSignInAccount): Observable<PartialLoginViewState> {
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        return loginService.signInWithCredential(credential)
                .map { PartialLoginViewState.LoginSuccess(it) }
                .cast(PartialLoginViewState::class.java)
                .onErrorResumeNext { throwable: Throwable -> emitErrorState(throwable as Exception) }
    }

    fun login(accessToken: AccessToken): Observable<PartialLoginViewState> {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        return loginService.signInWithCredential(credential)
                .map { PartialLoginViewState.LoginSuccess(it) }
                .cast(PartialLoginViewState::class.java)
                .onErrorResumeNext { throwable: Throwable -> emitErrorState(throwable as Exception) }
    }

    private fun emitErrorState(exception: Exception): Observable<PartialLoginViewState.ErrorState> {
        val errorState = PartialLoginViewState.ErrorState(exception, true)
        return Observable.just(errorState).startWith(errorState.copy(dismissToast = false))
    }
}