package com.boardly.home

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.boardly.R
import com.boardly.addevent.AddEventActivity
import com.boardly.base.BaseDrawerActivity
import com.boardly.factories.HomeViewModelFactory
import com.boardly.home.list.EventsAdapter
import com.boardly.home.models.Filter
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_home.addEventButton
import kotlinx.android.synthetic.main.activity_home.contentViewGroup
import kotlinx.android.synthetic.main.activity_home.eventsRecyclerView
import kotlinx.android.synthetic.main.activity_home.lookingForEventsTextView
import kotlinx.android.synthetic.main.activity_home.noEventsTextView
import javax.inject.Inject

class HomeActivity : BaseDrawerActivity(), HomeView {

    @Inject
    lateinit var homeViewModelFactory: HomeViewModelFactory

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var filteredFetchSubject: PublishSubject<Filter>

    private val eventsAdapter = EventsAdapter()

    private var init = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_home)
        super.onCreate(savedInstanceState)
        initRecyclerView()

        homeViewModel = ViewModelProviders.of(this, homeViewModelFactory)[HomeViewModel::class.java]
        addEventButton.setOnClickListener { startActivity(Intent(this, AddEventActivity::class.java)) }

        requestLocationPermission()
    }

    private fun initRecyclerView() {
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventsAdapter
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        homeViewModel.bind(this)
        if (init) filteredFetchSubject.onNext(Filter(1.0))
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.events_item)
    }

    override fun onStop() {
        init = false
        homeViewModel.unbind()
        super.onStop()
    }

    private fun initEmitters() {
        filteredFetchSubject = PublishSubject.create()
    }

    override fun emitFilteredFetchTrigger(): Observable<Filter> = filteredFetchSubject

    override fun render(homeViewState: HomeViewState) {
        with(homeViewState) {
            showNoEventsFoundText(false)
            showLookingForEventsText(progress)
            if (eventList.isNotEmpty() && !progress) {
                eventsAdapter.submitList(eventList)
            } else if (!progress) {
                showNoEventsFoundText(true)
            }
        }
    }

    private fun showLookingForEventsText(show: Boolean) {
        if (show) {
            lookingForEventsTextView.visibility = View.VISIBLE
            contentViewGroup.visibility = View.GONE
        } else {
            lookingForEventsTextView.visibility = View.GONE
            contentViewGroup.visibility = View.VISIBLE
        }
    }

    private fun showNoEventsFoundText(show: Boolean) {
        if (show) {
            contentViewGroup.visibility = View.GONE
            noEventsTextView.visibility = View.VISIBLE
        } else {
            contentViewGroup.visibility = View.VISIBLE
            noEventsTextView.visibility = View.GONE
        }
    }

    private fun requestLocationPermission() {
        RxPermissions(this)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe {
                    if (!it) {
                        finish()
                        Toast.makeText(this, R.string.location_permission_denied_text, Toast.LENGTH_LONG).show()
                    }
                }
    }
}