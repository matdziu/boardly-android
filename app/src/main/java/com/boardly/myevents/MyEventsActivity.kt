package com.boardly.myevents

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.boardly.R
import com.boardly.base.BaseDrawerActivity
import com.boardly.common.events.list.EventsAdapter
import com.boardly.factories.MyEventsViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_my_events.eventsRecyclerView
import kotlinx.android.synthetic.main.activity_my_events.noEventsTextView
import kotlinx.android.synthetic.main.activity_my_events.progressBar
import javax.inject.Inject

class MyEventsActivity : BaseDrawerActivity(), MyEventsView {

    private lateinit var fetchEventsSubject: PublishSubject<Boolean>

    private lateinit var myEventsViewModel: MyEventsViewModel

    private val eventsAdapter = EventsAdapter()

    @Inject
    lateinit var myEventsViewModelFactory: MyEventsViewModelFactory

    private var init = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_my_events)
        super.onCreate(savedInstanceState)
        initRecyclerView()

        myEventsViewModel = ViewModelProviders.of(this, myEventsViewModelFactory)[MyEventsViewModel::class.java]
    }

    private fun initRecyclerView() {
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventsAdapter
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
        init = false
    }

    override fun onStop() {
        myEventsViewModel.unbind()
        super.onStop()
    }

    private fun initEmitters() {
        fetchEventsSubject = PublishSubject.create()
    }

    override fun fetchEventsTriggerEmitter(): Observable<Boolean> = fetchEventsSubject

    override fun render(myEventsViewState: MyEventsViewState) {
        with(myEventsViewState) {
            showNoEventsFoundText(false)
            showProgressBar(progress)
            if (eventsList.isNotEmpty() && !progress) {
                eventsAdapter.submitList(eventsList)
            } else if (!progress) {
                showNoEventsFoundText(true)
            }
        }
    }

    private fun showNoEventsFoundText(show: Boolean) {
        if (show) {
            eventsRecyclerView.visibility = View.GONE
            noEventsTextView.visibility = View.VISIBLE
        } else {
            eventsRecyclerView.visibility = View.VISIBLE
            noEventsTextView.visibility = View.GONE
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            eventsRecyclerView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            eventsRecyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}