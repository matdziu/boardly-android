package com.boardly.login

import android.arch.lifecycle.ViewModel
import com.boardly.R
import com.boardly.constants.ERROR_INVALID_EMAIL
import com.boardly.constants.ERROR_USER_NOT_FOUND
import com.boardly.constants.ERROR_WRONG_PASSWORD
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class LoginViewModel(private val loginInteractor: LoginInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(LoginViewState())

    fun bind(loginView: LoginView) {
        val isLoggedInObservable = loginInteractor.isLoggedIn()

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
                isLoggedInObservable,
                googleSignInObservable,
                facebookSignInObservable))
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe({ loginView.render(it) }))
    }

    private fun reduce(previousState: LoginViewState, partialState: PartialLoginViewState)
            : LoginViewState {
        return when (partialState) {
            is PartialLoginViewState.LocalValidation -> LoginViewState(
                    emailValid = partialState.emailValid,
                    passwordValid = partialState.passwordValid)
            is PartialLoginViewState.InProgressState -> LoginViewState(true)
            is PartialLoginViewState.ErrorState -> LoginViewState(
                    errorMessageId = getErrorMessageId(partialState.exception),
                    error = true,
                    dismissToast = partialState.dismissToast)
            is PartialLoginViewState.LoginSuccess -> LoginViewState(
                    loginSuccess = true)
        }
    }

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

    fun unbind() {
        compositeDisposable.clear()
    }
}