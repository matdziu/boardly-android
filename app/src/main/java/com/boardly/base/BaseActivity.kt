package com.boardly.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.boardly.R
import com.boardly.injection.modules.GlideApp

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private val toolbar: Toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun showBackToolbarArrow(show: Boolean, backAction: () -> Unit = {}) {
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

    fun loadImageFromUrl(imageView: ImageView, pictureUrl: String) {
        GlideApp.with(this)
                .load(pictureUrl)
                .into(imageView)
    }
}