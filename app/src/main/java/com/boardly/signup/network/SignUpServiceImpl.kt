package com.boardly.signup.network

import com.boardly.base.BaseServiceImpl
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SignUpServiceImpl : SignUpService, BaseServiceImpl() {

    override fun createUserWithEmailAndPassword(email: String, password: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        resultSubject.onNext(true)
                    } else {
                        task.exception?.let { resultSubject.onError(it) }
                    }
                })
        return resultSubject
    }
}