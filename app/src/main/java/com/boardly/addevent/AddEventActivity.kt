package com.boardly.addevent

import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseActivity

class AddEventActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_add_event)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
    }
}