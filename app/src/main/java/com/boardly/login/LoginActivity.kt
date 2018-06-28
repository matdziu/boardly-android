package com.boardly.login

import android.content.Intent
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.signup.SignUpActivity
import kotlinx.android.synthetic.main.activity_login.createAccountButton

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_login)
        super.onCreate(savedInstanceState)

        createAccountButton.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }
    }
}