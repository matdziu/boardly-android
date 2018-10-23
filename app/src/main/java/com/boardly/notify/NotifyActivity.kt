package com.boardly.notify

import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseActivity
import dagger.android.AndroidInjection

class NotifyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_notify)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
    }
}