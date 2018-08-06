package com.boardly.myevents

import android.os.Bundle
import com.boardly.R
import com.boardly.base.BaseDrawerActivity

class MyEventsActivity : BaseDrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_my_events)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.my_events_item)
    }
}