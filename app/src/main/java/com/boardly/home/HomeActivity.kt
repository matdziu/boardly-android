package com.boardly.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.boardly.R
import com.boardly.addevent.AddEventActivity
import com.boardly.base.BaseDrawerActivity
import com.boardly.constants.PICKED_FILTER
import com.boardly.constants.PICK_FILTER_REQUEST_CODE
import com.boardly.factories.HomeViewModelFactory
import com.boardly.filter.FilterActivity
import com.boardly.filter.models.Filter
import com.boardly.home.list.EventsAdapter
import com.boardly.home.models.UserLocation
import com.google.android.gms.location.FusedLocationProviderClient
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

    private lateinit var filteredFetchSubject: PublishSubject<Pair<UserLocation, Filter>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val eventsAdapter = EventsAdapter()

    private var selectedFilter = Filter()

    private var emitFilter = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_home)
        super.onCreate(savedInstanceState)
        initRecyclerView()
        fusedLocationClient = FusedLocationProviderClient(this)

        homeViewModel = ViewModelProviders.of(this, homeViewModelFactory)[HomeViewModel::class.java]
        addEventButton.setOnClickListener { startActivity(Intent(this, AddEventActivity::class.java)) }

        requestLocationPermission()
    }

    private fun initRecyclerView() {
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventsAdapter
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        initEmitters()
        homeViewModel.bind(this)
        if (emitFilter && isLocationPermissionGranted()) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                filteredFetchSubject.onNext(Pair(UserLocation(it.latitude, it.longitude), selectedFilter))
            }
            emitFilter = false
        }
    }

    private fun initEmitters() {
        filteredFetchSubject = PublishSubject.create()
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.events_item)
    }

    override fun onStop() {
        homeViewModel.unbind()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_events_item -> startActivityForResult(
                    Intent(this, FilterActivity::class.java), PICK_FILTER_REQUEST_CODE)
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            when (requestCode) {
                PICK_FILTER_REQUEST_CODE -> handlePickFilterResult(resultCode, data)
            }
        }
    }

    private fun handlePickFilterResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                selectedFilter = data.getParcelableExtra(PICKED_FILTER)
                emitFilter = true
            }
        }
    }

    override fun filteredFetchTriggerEmitter(): Observable<Pair<UserLocation, Filter>> = filteredFetchSubject

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

    private fun isLocationPermissionGranted(): Boolean {
        return RxPermissions(this).isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
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