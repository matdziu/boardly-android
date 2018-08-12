package com.boardly.eventdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.constants.EVENT_ID
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_event_details.tabLayout
import kotlinx.android.synthetic.main.activity_event_details.viewPager
import javax.inject.Inject

class EventDetailsActivity : BaseActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private var eventId = ""

    companion object {
        fun start(context: Context, eventId: String) {
            val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra(EVENT_ID, eventId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_event_details)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
        eventId = intent.getStringExtra(EVENT_ID)

        initViewPager(eventId)
    }

    private fun initViewPager(eventId: String) {
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, tabLayout.tabCount, eventId)
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

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentDispatchingAndroidInjector
}