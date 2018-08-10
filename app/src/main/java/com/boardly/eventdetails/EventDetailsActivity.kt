package com.boardly.eventdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.constants.EVENT_ID
import kotlinx.android.synthetic.main.activity_event_details.tabLayout
import kotlinx.android.synthetic.main.activity_event_details.viewPager

class EventDetailsActivity : BaseActivity() {

    private var eventId = ""

    companion object {
        fun start(context: Context, eventId: String) {
            val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra(EVENT_ID, eventId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_event_details)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
        initViewPager()

        eventId = intent.getStringExtra(EVENT_ID)
    }

    private fun initViewPager() {
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // unused
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // unused
            }
        })
    }
}