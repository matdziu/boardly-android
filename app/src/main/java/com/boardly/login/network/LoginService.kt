package com.boardly.login.network

import com.boardly.login.models.LoginData
import com.google.firebase.auth.AuthCredential
import io.reactivex.Observable

interface LoginService {

    fun login(email: String, password: String): Observable<Boolean>

    fun signInWithCredential(credential: AuthCredential): Observable<Boolean>

    fun isLoggedIn(): Observable<LoginData>
}