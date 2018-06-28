package com.boardly.signup

import io.reactivex.Observable

interface SignUpView {

    fun render(signUpViewState: SignUpViewState)

    fun emitInput(): Observable<InputData>
}