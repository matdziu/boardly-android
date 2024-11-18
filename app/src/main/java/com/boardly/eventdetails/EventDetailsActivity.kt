package com.boardly.eventdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import com.boardly.base.BaseActivity
import com.boardly.common.events.models.EventType
import com.boardly.constants.EVENT_ID
import com.boardly.constants.EVENT_TYPE
import com.boardly.databinding.ActivityEventDetailsBinding
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class EventDetailsActivity : BaseActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    var eventId = ""
        private set
    private var eventType = EventType.ACCEPTED

    private lateinit var binding: ActivityEventDetailsBinding

    companion object {
        fun start(context: Context, eventId: String, eventType: EventType) {
            val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra(EVENT_ID, eventId)
            intent.putExtra(EVENT_TYPE, eventType)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        eventId = intent.getStringExtra(EVENT_ID) ?: ""
        eventType = intent.getSerializableExtra(EVENT_TYPE) as EventType

        initViewPager(eventId, eventType)
    }

    private fun initViewPager(eventId: String, eventType: EventType) {
        binding.viewPager.adapter =
            ViewPagerAdapter(supportFragmentManager, binding.tabLayout.tabCount, eventId, eventType)
        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(binding.viewPager))
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> =
        fragmentDispatchingAndroidInjector
}