package com.boardly.home

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.boardly.R
import com.boardly.base.joinevent.BaseJoinEventActivity
import com.boardly.common.events.list.EventsAdapter
import com.boardly.common.location.UserLocation
import com.boardly.constants.PICKED_FILTER
import com.boardly.constants.PICK_FILTER_REQUEST_CODE
import com.boardly.constants.SAVED_GAME_ID
import com.boardly.constants.SAVED_GAME_NAME
import com.boardly.constants.SAVED_IS_CURRENT_LOCATION
import com.boardly.constants.SAVED_LOCATION_LATITUDE
import com.boardly.constants.SAVED_LOCATION_LONGITUDE
import com.boardly.constants.SAVED_LOCATION_NAME
import com.boardly.constants.SAVED_RADIUS
import com.boardly.databinding.ActivityHomeBinding
import com.boardly.discover.DiscoverActivity
import com.boardly.event.EventActivity
import com.boardly.factories.HomeViewModelFactory
import com.boardly.filter.FilterActivity
import com.boardly.filter.models.Filter
import com.boardly.home.models.FilteredFetchData
import com.boardly.notify.NotifyActivity
import com.google.android.gms.common.GoogleApiAvailability
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class HomeActivity : BaseJoinEventActivity(), HomeView {

    @Inject
    lateinit var homeViewModelFactory: HomeViewModelFactory

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var filteredFetchSubject: PublishSubject<FilteredFetchData>
    private lateinit var locationProcessingSubject: PublishSubject<Boolean>

    private val eventsAdapter = EventsAdapter()
    private var selectedFilter = Filter()
    private var init = true

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        initRecyclerView()

        selectedFilter = getSavedFilter()
        saveFilter(selectedFilter, PreferenceManager.getDefaultSharedPreferences(this))

        homeViewModel = ViewModelProviders.of(this, homeViewModelFactory)[HomeViewModel::class.java]
        binding.addEventButton.setOnClickListener { EventActivity.startAddMode(this@HomeActivity) }
        binding.discoverButton.setOnClickListener { DiscoverActivity.start(this@HomeActivity) }
        binding.inviteFriendsButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.boardly_invite_text))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.chooser_title_text)))
        }
    }

    private fun initRecyclerView() {
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.eventsRecyclerView.adapter = eventsAdapter
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        homeViewModel.bind(this)
        if (selectedFilter.isCurrentLocation) {
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
        locationProcessingSubject = PublishSubject.create()
    }

    private fun getLocationAndEmitFilter() {
        locationProcessingSubject.onNext(init)
        val onLocationFound = { location: Location ->
            selectedFilter.userLocation = UserLocation(location.latitude, location.longitude)
            selectedFilter.locationName = getString(R.string.current_location_info)
            selectedFilter.isCurrentLocation = true
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
            R.id.notify_item -> startActivity(Intent(this, NotifyActivity::class.java))
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
                selectedFilter = data.getParcelableExtra(PICKED_FILTER) ?: Filter()
                init = true
                saveFilter(selectedFilter)
            }
        }
    }

    override fun filteredFetchTriggerEmitter(): Observable<FilteredFetchData> = filteredFetchSubject

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

            showJoinRequestSentToast(joinRequestSent)
        }
    }

    private fun showLocationProcessingText(locationProcessing: Boolean) {
        if (locationProcessing) {
            binding.locationProcessingTextView.visibility = View.VISIBLE
        } else {
            binding.locationProcessingTextView.visibility = View.GONE
        }
    }

    private fun showLookingForEventsText(show: Boolean) {
        if (show) {
            binding.lookingForEventsTextView.visibility = View.VISIBLE
            binding.contentViewGroup.visibility = View.GONE
        } else {
            binding.lookingForEventsTextView.visibility = View.GONE
            binding.contentViewGroup.visibility = View.VISIBLE
        }
    }

    private fun showNoEventsFoundText(show: Boolean) {
        if (show) {
            binding.contentViewGroup.visibility = View.GONE
            binding.noEventsTextView.visibility = View.VISIBLE
        } else {
            binding.contentViewGroup.visibility = View.VISIBLE
            binding.noEventsTextView.visibility = View.GONE
        }
    }

    private fun showNoLocationPermissionText(show: Boolean) {
        if (show) {
            binding.noLocationPermissionTextView.visibility = View.VISIBLE
        } else {
            binding.noLocationPermissionTextView.visibility = View.GONE
        }
    }

    private fun getSavedFilter(): Filter {
        val sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        val savedRadius = sharedPrefs.getInt(SAVED_RADIUS, 50)
        val savedGameId = sharedPrefs.getString(SAVED_GAME_ID, "") ?: ""
        val savedGameName = sharedPrefs.getString(SAVED_GAME_NAME, "") ?: ""
        val savedLocationName = sharedPrefs.getString(SAVED_LOCATION_NAME, "") ?: ""
        val savedLocationLongitude = sharedPrefs.getString(SAVED_LOCATION_LONGITUDE, "") ?: ""
        val savedLocationLatitude = sharedPrefs.getString(SAVED_LOCATION_LATITUDE, "") ?: ""
        val savedIsCurrentLocation = sharedPrefs.getBoolean(SAVED_IS_CURRENT_LOCATION, true)
        val userLocation = getUserLocationFromString(savedLocationLongitude, savedLocationLatitude)
        return Filter(
            savedRadius.toDouble(),
            savedGameId,
            savedGameName,
            userLocation,
            savedLocationName,
            savedIsCurrentLocation
        )
    }

    private fun getUserLocationFromString(longitude: String, latitude: String): UserLocation? {
        return if (longitude.isNotEmpty() && latitude.isNotEmpty() && longitude != "null" && latitude != "null") UserLocation(
            latitude.toDouble(),
            longitude.toDouble()
        )
        else null
    }

    private fun saveFilter(filter: Filter) {
        val sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        val commonSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        saveFilter(filter, sharedPrefs)
        saveFilter(filter, commonSharedPrefs)
    }

    private fun saveFilter(filter: Filter, sharedPrefs: SharedPreferences) {
        sharedPrefs.edit()
            .putInt(SAVED_RADIUS, filter.radius.toInt())
            .putString(SAVED_GAME_ID, filter.gameId)
            .putString(SAVED_GAME_NAME, filter.gameName)
            .putString(SAVED_LOCATION_NAME, filter.locationName)
            .putString(SAVED_LOCATION_LATITUDE, filter.userLocation?.latitude.toString())
            .putString(SAVED_LOCATION_LONGITUDE, filter.userLocation?.longitude.toString())
            .putBoolean(SAVED_IS_CURRENT_LOCATION, filter.isCurrentLocation)
            .apply()
    }
}