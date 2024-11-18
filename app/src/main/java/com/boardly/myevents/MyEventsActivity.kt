package com.boardly.myevents

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.boardly.R
import com.boardly.base.joinevent.BaseJoinEventActivity
import com.boardly.common.events.models.Event
import com.boardly.constants.LAUNCH_INFO
import com.boardly.databinding.ActivityMyEventsBinding
import com.boardly.extensions.setBackgroundColor
import com.boardly.extensions.setMaxLines
import com.boardly.extensions.setTextColor
import com.boardly.extensions.simplySetActionTextColor
import com.boardly.factories.MyEventsViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class MyEventsActivity : BaseJoinEventActivity(), MyEventsView {

    private lateinit var fetchEventsSubject: PublishSubject<Boolean>

    private lateinit var myEventsViewModel: MyEventsViewModel

    private val viewPagerAdapter = ViewPagerAdapter()

    @Inject
    lateinit var myEventsViewModelFactory: MyEventsViewModelFactory

    private var init = true
    private var viewPagerInit = false

    private lateinit var binding: ActivityMyEventsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        binding = ActivityMyEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        initViewPager()

        myEventsViewModel =
            ViewModelProviders.of(this, myEventsViewModelFactory)[MyEventsViewModel::class.java]

        val launchInfo = intent.getStringExtra(LAUNCH_INFO)
        if (launchInfo != null) {
            Snackbar.make(binding.coordinatorLayout, launchInfo, Snackbar.LENGTH_INDEFINITE)
                .simplySetActionTextColor(android.R.color.white)
                .setBackgroundColor(R.color.colorPrimary)
                .setTextColor(android.R.color.white)
                .setMaxLines(6)
                .setAction(R.string.snackbar_ok, {})
                .show()
            binding.viewPager.currentItem = 3
        }
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
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(binding.viewPager))
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
                showNoEventsTextView(
                    acceptedEvents,
                    binding.viewPager.findViewWithTag(PageView.ACCEPTED)
                )
                showNoEventsTextView(
                    pendingEvents,
                    binding.viewPager.findViewWithTag(PageView.PENDING)
                )
                showNoEventsTextView(
                    createdEvents,
                    binding.viewPager.findViewWithTag(PageView.CREATED)
                )
                showNoEventsTextView(
                    interestingEvents,
                    binding.viewPager.findViewWithTag(PageView.INTERESTING)
                )
            }

            showJoinRequestSentToast(joinRequestSent)
        }
    }

    private fun showNoEventsTextView(eventsList: List<Event>, pageView: View) {
        with(pageView) {
            val noEventsTextView = this.findViewById<TextView>(R.id.noEventsTextView)
            val myEventsRecyclerView = this.findViewById<RecyclerView>(R.id.myEventsRecyclerView)
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
            binding.viewPager.visibility = View.GONE
            binding.tabLayout.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.viewPager.visibility = View.VISIBLE
            binding.tabLayout.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}