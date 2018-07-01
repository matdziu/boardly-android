package com.boardly.login

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class LoginViewModel(private val loginInteractor: LoginInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(LoginViewState())

    fun bind(loginView: LoginView) {
        val initialLoginCheckObservable = loginView.emitInitialLoginCheck()
                .filter { it }
                .flatMap { loginInteractor.isLoggedIn().startWith(PartialLoginViewState.InProgressState()) }

        val googleSignInObservable = loginView.emitGoogleSignIn()
                .flatMap { loginInteractor.login(it).startWith(PartialLoginViewState.InProgressState()) }

        val facebookSignInObservable = loginView.emitFacebookSignIn()
                .flatMap { loginInteractor.login(it).startWith(PartialLoginViewState.InProgressState()) }

        val inputDataObservable = loginView.emitInput()
                .flatMap { inputData ->
                    val trimmedEmail = inputData.email.trim()
                    val trimmedPassword = inputData.password.trim()

                    val emailValid = !trimmedEmail.isBlank()
                    val passwordValid = !trimmedPassword.isBlank()

                    return@flatMap if (!emailValid || !passwordValid) {
                        Observable.just(PartialLoginViewState.LocalValidation(emailValid, passwordValid))
                    } else {
                        loginInteractor.login(trimmedEmail, trimmedPassword)
                                .startWith(PartialLoginViewState.InProgressState())
                    }
                }

        val mergedObservable = Observable.merge(listOf(
                inputDataObservable,
                initialLoginCheckObservable,
                googleSignInObservable,
                facebookSignInObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe({ loginView.render(it) }))
    }

    private fun reduce(previousState: LoginViewState, partialState: PartialLoginViewState)
            : LoginViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}