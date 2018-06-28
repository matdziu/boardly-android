package com.boardly.signup

import android.support.annotation.StringRes

data class SignUpViewState(val inProgress: Boolean = false,
                           val emailValid: Boolean = true,
                           val passwordValid: Boolean = true,
                           val error: Boolean = false,
                           @StringRes val errorMessageId: Int = 0,
                           val dismissToast: Boolean = false,
                           val signUpSuccess: Boolean = false)