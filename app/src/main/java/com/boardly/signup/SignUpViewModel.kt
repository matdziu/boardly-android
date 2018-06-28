package com.boardly.signup

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class SignUpViewModel(private val signUpInteractor: SignUpInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(SignUpViewState())

    fun bind(signUpView: SignUpView) {
        val inputDataObservable = signUpView.emitInput()
                .flatMap { inputData ->
                    val trimmedEmail = inputData.email.trim()
                    val trimmedPassword = inputData.password.trim()

                    val emailValid = !trimmedEmail.isBlank() && !trimmedEmail.contains(" ")
                    val passwordValid = trimmedPassword.length >= 6 && !trimmedPassword.contains(" ")

                    return@flatMap if (!emailValid || !passwordValid) {
                        Observable.just(PartialSignUpViewState.LocalValidation(emailValid, passwordValid))
                    } else {
                        signUpInteractor.createAccount(trimmedEmail, trimmedPassword)
                                .startWith(PartialSignUpViewState.InProgressState())
                    }
                }
                .scan(stateSubject.value, BiFunction(this::reduce))
                .subscribeWith(stateSubject)

        compositeDisposable.add(inputDataObservable.subscribe({ signUpView.render(it) }))
    }

    private fun reduce(previousState: SignUpViewState, partialState: PartialSignUpViewState)
            : SignUpViewState {
        return partialState.reduce(previousState)
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}