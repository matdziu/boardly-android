package com.boardly.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.boardly.R

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
}