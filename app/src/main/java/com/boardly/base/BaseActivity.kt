package com.boardly.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.boardly.BoardlyApplication
import com.boardly.R
import com.boardly.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    protected val toolbar: Toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    open fun showBackToolbarArrow(show: Boolean, backAction: () -> Unit = {}) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
        supportActionBar?.setDisplayShowHomeEnabled(show)
        toolbar.setNavigationOnClickListener { backAction() }
    }

    fun hideSoftKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun signOut() {
        val boardlyApplication = application as BoardlyApplication
        if (boardlyApplication.online) {
            Thread { FirebaseInstanceId.getInstance().deleteInstanceId() }.start()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            Toast.makeText(this, getString(R.string.logout_connection_prompt), Toast.LENGTH_LONG).show()
        }
    }
}