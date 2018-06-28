package com.boardly.login

import android.support.annotation.StringRes

data class LoginViewState(val inProgress: Boolean = false,
                          val emailValid: Boolean = true,
                          val passwordValid: Boolean = true,
                          @StringRes val errorMessageId: Int = 0,
                          val error: Boolean = false,
                          val dismissToast: Boolean = false,
                          val loginSuccess: Boolean = false)