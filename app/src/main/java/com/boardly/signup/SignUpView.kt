package com.boardly.signup

import com.boardly.signup.models.InputData
import io.reactivex.Observable

interface SignUpView {

    fun render(signUpViewState: SignUpViewState)

    fun emitInput(): Observable<InputData>
}