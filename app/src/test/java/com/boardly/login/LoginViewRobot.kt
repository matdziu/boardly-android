package com.boardly.login

import com.boardly.base.BaseViewRobot
import com.boardly.login.models.InputData
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class LoginViewRobot(loginViewModel: LoginViewModel) : BaseViewRobot<LoginViewState>() {

    private val inputObservable: Subject<InputData> = PublishSubject.create()
    private val initialLoginCheckSubject = PublishSubject.create<Boolean>()

    private val loginView = object : LoginView {
        override fun emitInitialLoginCheck(): Observable<Boolean> = initialLoginCheckSubject

        override fun emitFacebookSignIn(): Observable<AccessToken> = Completable.complete().toObservable()

        override fun emitGoogleSignIn(): Observable<GoogleSignInAccount> = Completable.complete().toObservable()

        override fun render(loginViewState: LoginViewState) {
            renderedStates.add(loginViewState)
        }

        override fun emitInput(): Observable<InputData> = inputObservable

    }

    init {
        loginViewModel.bind(loginView)
        emitInitialLoginCheck()
    }

    fun clickLoginButton(email: String, password: String) {
        inputObservable.onNext(InputData(email, password))
    }

    private fun emitInitialLoginCheck() {
        initialLoginCheckSubject.onNext(true)
    }
}