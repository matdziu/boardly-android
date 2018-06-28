package com.boardly.signup

import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class SignUpInteractor {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun createAccount(email: String, password: String): Observable<PartialSignUpViewState> {
        val stateSubject: Subject<PartialSignUpViewState> = PublishSubject.create()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        stateSubject.onNext(PartialSignUpViewState.SignUpSuccess())
                    } else {
                        val errorState = PartialSignUpViewState.ErrorState(task.exception)
                        stateSubject.onNext(errorState)
                        stateSubject.onNext(errorState.copy(dismissToast = true))
                    }
                })
        return stateSubject.observeOn(AndroidSchedulers.mainThread())
    }
}