package com.boardly.filter

import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseActivity

class FilterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_filter)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
    }
}