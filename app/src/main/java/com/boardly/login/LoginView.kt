package com.boardly.login

import com.boardly.login.models.InputData
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.Observable

interface LoginView {

    fun render(loginViewState: LoginViewState)

    fun inputEmitter(): Observable<InputData>

    fun googleSignInEmitter(): Observable<GoogleSignInAccount>

    fun facebookSignInEmitter(): Observable<AccessToken>

    fun initialLoginCheckEmitter(): Observable<Boolean>
}