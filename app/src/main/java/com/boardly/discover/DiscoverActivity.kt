package com.boardly.discover

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseActivity
import dagger.android.AndroidInjection

class DiscoverActivity : BaseActivity() {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, DiscoverActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        AndroidInjection.inject(this)
        setContentView(R.layout.activity_discover)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
    }
}