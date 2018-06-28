package com.boardly.signup

import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseActivity

class SignUpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_sign_up)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
    }
}