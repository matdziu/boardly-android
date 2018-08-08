package com.boardly.signup.network

import io.reactivex.Observable

interface SignUpService {

    fun createUserWithEmailAndPassword(email: String, password: String): Observable<Boolean>
}