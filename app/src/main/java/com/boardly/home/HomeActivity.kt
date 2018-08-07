package com.boardly.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
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
import com.boardly.common.events.list.EventsAdapter
import com.boardly.constants.PICKED_FILTER
import com.boardly.constants.PICK_FILTER_REQUEST_CODE
import com.boardly.constants.SAVED_GAME_ID
import com.boardly.constants.SAVED_GAME_NAME
import com.boardly.constants.SAVED_RADIUS
import com.boardly.factories.HomeViewModelFactory
import com.boardly.filter.FilterActivity
import com.boardly.filter.models.Filter
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
import kotlinx.android.synthetic.main.activity_home.noLocationPermissionTextView
import javax.inject.Inject

class HomeActivity : BaseDrawerActivity(), HomeView {

    @Inject
    lateinit var homeViewModelFactory: HomeViewModelFactory

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var filteredFetchSubject: PublishSubject<Pair<UserLocation, Filter>>
    lateinit var joinEventSubject: PublishSubject<String>
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
        selectedFilter = getSavedFilter()

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
        if (emitFilter && isLocationPermissionGranted()) {
            emitFilteredFetchTrigger()
            emitFilter = false
        }
    }

    private fun initEmitters() {
        filteredFetchSubject = PublishSubject.create()
        joinEventSubject = PublishSubject.create()
    }

    @SuppressLint("MissingPermission")
    private fun emitFilteredFetchTrigger() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            filteredFetchSubject.onNext(Pair(UserLocation(it.latitude, it.longitude), selectedFilter))
        }
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
            R.id.filter_events_item -> FilterActivity.start(this, selectedFilter)
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
                saveFilter(selectedFilter)
                emitFilter = true
            }
        }
    }

    override fun filteredFetchTriggerEmitter(): Observable<Pair<UserLocation, Filter>> = filteredFetchSubject

    override fun joinEventEmitter(): Observable<String> = joinEventSubject

    override fun render(homeViewState: HomeViewState) {
        with(homeViewState) {
            showNoEventsFoundText(false)
            showNoLocationPermissionText(!isLocationPermissionGranted() && !progress)
            showLookingForEventsText(progress)
            if (eventList.isNotEmpty() && !progress) {
                eventsAdapter.submitList(eventList)
            } else if (!progress && isLocationPermissionGranted()) {
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

    private fun showNoLocationPermissionText(show: Boolean) {
        if (show) {
            noLocationPermissionTextView.visibility = View.VISIBLE
        } else {
            noLocationPermissionTextView.visibility = View.GONE
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
                    } else {
                        emitFilteredFetchTrigger()
                    }
                }
    }

    private fun getSavedFilter(): Filter {
        val sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        val savedRadius = sharedPrefs.getInt(SAVED_RADIUS, 50)
        val savedGameId = sharedPrefs.getString(SAVED_GAME_ID, "")
        val savedGameName = sharedPrefs.getString(SAVED_GAME_NAME, "")
        return Filter(savedRadius.toDouble(), savedGameId, savedGameName)
    }

    private fun saveFilter(filter: Filter) {
        val sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        sharedPrefs.edit()
                .putInt(SAVED_RADIUS, selectedFilter.radius.toInt())
                .putString(SAVED_GAME_ID, filter.gameId)
                .putString(SAVED_GAME_NAME, filter.gameName)
                .apply()
    }
}