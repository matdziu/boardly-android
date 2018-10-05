package com.boardly.home

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseDrawerActivity
import com.boardly.common.events.list.EventsAdapter
import com.boardly.common.location.UserLocation
import com.boardly.constants.PICKED_FILTER
import com.boardly.constants.PICK_FILTER_REQUEST_CODE
import com.boardly.constants.SAVED_GAME_ID
import com.boardly.constants.SAVED_GAME_NAME
import com.boardly.constants.SAVED_LOCATION_LATITUDE
import com.boardly.constants.SAVED_LOCATION_LONGITUDE
import com.boardly.constants.SAVED_LOCATION_NAME
import com.boardly.constants.SAVED_RADIUS
import com.boardly.event.EventActivity
import com.boardly.factories.HomeViewModelFactory
import com.boardly.filter.FilterActivity
import com.boardly.filter.models.Filter
import com.boardly.home.models.FilteredFetchData
import com.boardly.home.models.JoinEventData
import com.google.android.gms.common.GoogleApiAvailability
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_home.addEventButton
import kotlinx.android.synthetic.main.activity_home.contentViewGroup
import kotlinx.android.synthetic.main.activity_home.eventsRecyclerView
import kotlinx.android.synthetic.main.activity_home.inviteFriendsButton
import kotlinx.android.synthetic.main.activity_home.locationProcessingTextView
import kotlinx.android.synthetic.main.activity_home.lookingForEventsTextView
import kotlinx.android.synthetic.main.activity_home.noEventsTextView
import kotlinx.android.synthetic.main.activity_home.noLocationPermissionTextView
import javax.inject.Inject

class HomeActivity : BaseDrawerActivity(), HomeView {

    @Inject
    lateinit var homeViewModelFactory: HomeViewModelFactory

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var filteredFetchSubject: PublishSubject<FilteredFetchData>
    private lateinit var locationProcessingSubject: PublishSubject<Boolean>
    lateinit var joinEventSubject: PublishSubject<JoinEventData>

    private val eventsAdapter = EventsAdapter()
    private var selectedFilter = Filter()
    private var init = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_home)
        super.onCreate(savedInstanceState)
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        initRecyclerView()
        selectedFilter = getSavedFilter()

        homeViewModel = ViewModelProviders.of(this, homeViewModelFactory)[HomeViewModel::class.java]
        addEventButton.setOnClickListener { EventActivity.startAddMode(this@HomeActivity) }
        inviteFriendsButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.boardly_invite_text))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.chooser_title_text)))
        }
    }

    private fun initRecyclerView() {
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventsAdapter
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        homeViewModel.bind(this)
        if (selectedFilter.userLocation == null) {
            checkLocationSettings({
                showNoLocationPermissionText(false)
                getLocationAndEmitFilter()
            }, {
                showNoLocationPermissionText(true)
            })
        } else {
            filteredFetchSubject.onNext(FilteredFetchData(selectedFilter, init))
        }
    }

    private fun initEmitters() {
        filteredFetchSubject = PublishSubject.create()
        joinEventSubject = PublishSubject.create()
        locationProcessingSubject = PublishSubject.create()
    }

    private fun getLocationAndEmitFilter() {
        locationProcessingSubject.onNext(init)
        val onLocationFound = { location: Location ->
            selectedFilter.userLocation = UserLocation(location.latitude, location.longitude)
            selectedFilter.locationName = getString(R.string.current_location_info)
            saveFilter(selectedFilter)
            filteredFetchSubject.onNext(FilteredFetchData(selectedFilter, init))
        }
        getLastKnownLocation { onLocationFound(it) }
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.events_item)
    }

    override fun onStop() {
        homeViewModel.unbind()
        init = false
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
        super.onActivityResult(requestCode, resultCode, data)
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
                init = true
                saveFilter(selectedFilter)
            }
        }
    }

    override fun filteredFetchTriggerEmitter(): Observable<FilteredFetchData> = filteredFetchSubject

    override fun joinEventEmitter(): Observable<JoinEventData> = joinEventSubject

    override fun locationProcessingEmitter(): Observable<Boolean> = locationProcessingSubject

    override fun render(homeViewState: HomeViewState) {
        with(homeViewState) {
            showNoEventsFoundText(false)
            showNoLocationPermissionText(!isLocationPermissionGranted() && !progress)
            showLookingForEventsText(progress)
            showLocationProcessingText(locationProcessing)
            eventsAdapter.submitList(eventList)

            if (!progress && !locationProcessing && eventList.isNotEmpty()) {
                showNoLocationPermissionText(false)
            } else if (eventList.isEmpty() && !progress && !locationProcessing && isLocationPermissionGranted()) {
                showNoEventsFoundText(true)
            }

            if (joinRequestSent) Toast.makeText(this@HomeActivity, R.string.join_request_sent, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLocationProcessingText(locationProcessing: Boolean) {
        if (locationProcessing) {
            locationProcessingTextView.visibility = View.VISIBLE
        } else {
            locationProcessingTextView.visibility = View.GONE
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

    private fun getSavedFilter(): Filter {
        val sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        val savedRadius = sharedPrefs.getInt(SAVED_RADIUS, 50)
        val savedGameId = sharedPrefs.getString(SAVED_GAME_ID, "")
        val savedGameName = sharedPrefs.getString(SAVED_GAME_NAME, "")
        val savedLocationName = sharedPrefs.getString(SAVED_LOCATION_NAME, "")
        val savedLocationLongitude = sharedPrefs.getString(SAVED_LOCATION_LONGITUDE, "")
        val savedLocationLatitude = sharedPrefs.getString(SAVED_LOCATION_LATITUDE, "")
        val userLocation = getUserLocationFromString(savedLocationLongitude, savedLocationLatitude)
        return Filter(savedRadius.toDouble(), savedGameId, savedGameName, userLocation, savedLocationName)
    }

    private fun getUserLocationFromString(longitude: String, latitude: String): UserLocation? {
        return if (longitude.isNotEmpty() && latitude.isNotEmpty()) UserLocation(latitude.toDouble(), longitude.toDouble())
        else null
    }

    private fun saveFilter(filter: Filter) {
        val sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        sharedPrefs.edit()
                .putInt(SAVED_RADIUS, filter.radius.toInt())
                .putString(SAVED_GAME_ID, filter.gameId)
                .putString(SAVED_GAME_NAME, filter.gameName)
                .putString(SAVED_LOCATION_NAME, filter.locationName)
                .putString(SAVED_LOCATION_LATITUDE, filter.userLocation?.latitude.toString())
                .putString(SAVED_LOCATION_LONGITUDE, filter.userLocation?.longitude.toString())
                .apply()
    }
}