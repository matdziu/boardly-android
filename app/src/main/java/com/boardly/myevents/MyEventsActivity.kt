package com.boardly.myevents

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.boardly.R
import com.boardly.base.BaseDrawerActivity
import com.boardly.factories.MyEventsViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_my_events.progressBar
import kotlinx.android.synthetic.main.activity_my_events.tabLayout
import kotlinx.android.synthetic.main.activity_my_events.viewPager
import javax.inject.Inject

class MyEventsActivity : BaseDrawerActivity(), MyEventsView {

    private lateinit var fetchEventsSubject: PublishSubject<Boolean>

    private lateinit var myEventsViewModel: MyEventsViewModel

    @Inject
    lateinit var myEventsViewModelFactory: MyEventsViewModelFactory

    private var init = true
    private var viewPagerPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_my_events)
        super.onCreate(savedInstanceState)
        initViewPager()

        myEventsViewModel = ViewModelProviders.of(this, myEventsViewModelFactory)[MyEventsViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.my_events_item)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        myEventsViewModel.bind(this)

        fetchEventsSubject.onNext(init)
    }

    override fun onStop() {
        myEventsViewModel.unbind()
        init = false
        viewPagerPosition = viewPager.currentItem
        super.onStop()
    }

    private fun initEmitters() {
        fetchEventsSubject = PublishSubject.create()
    }

    private fun initViewPager() {
        viewPager.offscreenPageLimit = 2
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
    }

    override fun fetchEventsTriggerEmitter(): Observable<Boolean> = fetchEventsSubject

    override fun render(myEventsViewState: MyEventsViewState) {
        with(myEventsViewState) {
            showProgressBar(progress)
            viewPager.adapter = ViewPagerAdapter(acceptedEvents, pendingEvents, createdEvents)
            viewPager.setCurrentItem(viewPagerPosition, false)
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            viewPager.visibility = View.GONE
            tabLayout.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            viewPager.visibility = View.VISIBLE
            tabLayout.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}