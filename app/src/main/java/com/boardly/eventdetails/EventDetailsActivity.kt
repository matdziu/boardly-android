package com.boardly.eventdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.common.events.models.Event
import com.boardly.constants.EVENT
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

    private var event = Event()

    companion object {
        fun start(context: Context, event: Event) {
            val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra(EVENT, event)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_event_details)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
        event = intent.getParcelableExtra(EVENT)

        initViewPager(event)
    }

    private fun initViewPager(event: Event) {
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, tabLayout.tabCount, event)
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