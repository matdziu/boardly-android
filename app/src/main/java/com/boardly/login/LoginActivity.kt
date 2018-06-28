package com.boardly.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.boardly.R
import com.boardly.signup.SignUpActivity
import kotlinx.android.synthetic.main.activity_login.createAccountButton

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        createAccountButton.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }
    }
}