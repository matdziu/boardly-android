package com.boardly.myevents

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.boardly.R
import com.boardly.base.BaseDrawerActivity
import com.boardly.common.events.models.Event
import com.boardly.factories.MyEventsViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_my_events.progressBar
import kotlinx.android.synthetic.main.activity_my_events.tabLayout
import kotlinx.android.synthetic.main.activity_my_events.viewPager
import kotlinx.android.synthetic.main.view_my_events.view.myEventsRecyclerView
import kotlinx.android.synthetic.main.view_my_events.view.noEventsTextView
import javax.inject.Inject

class MyEventsActivity : BaseDrawerActivity(), MyEventsView {

    private lateinit var fetchEventsSubject: PublishSubject<Boolean>

    private lateinit var myEventsViewModel: MyEventsViewModel

    private val viewPagerAdapter = ViewPagerAdapter()

    @Inject
    lateinit var myEventsViewModelFactory: MyEventsViewModelFactory

    private var init = true
    private var viewPagerInit = false

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
        super.onStop()
    }

    private fun initEmitters() {
        fetchEventsSubject = PublishSubject.create()
    }

    private fun initViewPager() {
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = viewPagerAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
    }

    override fun fetchEventsTriggerEmitter(): Observable<Boolean> = fetchEventsSubject

    override fun render(myEventsViewState: MyEventsViewState) {
        with(myEventsViewState) {
            showProgressBar(progress)
            viewPagerAdapter.pendingAdapter.submitList(pendingEvents)
            viewPagerAdapter.acceptedAdapter.submitList(acceptedEvents)
            viewPagerAdapter.createdAdapter.submitList(createdEvents)
            viewPagerAdapter.interestingAdapter.submitList(interestingEvents)

            if (!viewPagerInit) {
                // this is done because of surprisingly very late call to instantiateItem()
                viewPagerAdapter.renderingFinishedEmitter().subscribe {
                    viewPagerInit = true
                    when (it.tag) {
                        PageView.ACCEPTED -> showNoEventsTextView(acceptedEvents, it)
                        PageView.CREATED -> showNoEventsTextView(createdEvents, it)
                        PageView.PENDING -> showNoEventsTextView(pendingEvents, it)
                        PageView.INTERESTING -> showNoEventsTextView(interestingEvents, it)
                    }
                }
            } else {
                showNoEventsTextView(acceptedEvents, viewPager.findViewWithTag(PageView.ACCEPTED))
                showNoEventsTextView(pendingEvents, viewPager.findViewWithTag(PageView.PENDING))
                showNoEventsTextView(createdEvents, viewPager.findViewWithTag(PageView.CREATED))
                showNoEventsTextView(interestingEvents, viewPager.findViewWithTag(PageView.INTERESTING))
            }
        }
    }

    private fun showNoEventsTextView(eventsList: List<Event>, pageView: View) {
        with(pageView) {
            if (eventsList.isEmpty()) {
                noEventsTextView.visibility = View.VISIBLE
                myEventsRecyclerView.visibility = View.GONE
            } else {
                noEventsTextView.visibility = View.GONE
                myEventsRecyclerView.visibility = View.VISIBLE
            }
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