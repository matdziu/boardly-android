package com.boardly.discover

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.common.location.UserLocation
import com.boardly.constants.SAVED_LOCATION_LATITUDE
import com.boardly.constants.SAVED_LOCATION_LONGITUDE
import com.boardly.constants.SAVED_RADIUS
import com.boardly.discover.models.FilteredFetchData
import com.boardly.factories.DiscoverViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_discover.noUserLocationTextView
import javax.inject.Inject

class DiscoverActivity : BaseActivity(), DiscoverView {

    @Inject
    lateinit var discoverViewModelFactory: DiscoverViewModelFactory

    private lateinit var discoverViewModel: DiscoverViewModel

    private lateinit var fetchPlacesListTriggerSubject: PublishSubject<FilteredFetchData>

    private var init = true

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, DiscoverActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_discover)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
        discoverViewModel = ViewModelProviders.of(this, discoverViewModelFactory)[DiscoverViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        discoverViewModel.bind(this)
        if (init) initFetching()
    }

    private fun initFetching() {
        val sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        val savedRadius = sharedPrefs.getInt(SAVED_RADIUS, 50)
        val savedLocationLongitude = sharedPrefs.getString(SAVED_LOCATION_LONGITUDE, "")
        val savedLocationLatitude = sharedPrefs.getString(SAVED_LOCATION_LATITUDE, "")
        val userLocation = getUserLocationFromString(savedLocationLongitude, savedLocationLatitude)

        if (userLocation != null) fetchPlacesListTriggerSubject.onNext(FilteredFetchData(userLocation, savedRadius.toDouble()))
        else noUserLocationTextView.visibility = View.VISIBLE
    }

    private fun getUserLocationFromString(longitude: String, latitude: String): UserLocation? {
        return if (longitude.isNotEmpty() && latitude.isNotEmpty()) UserLocation(latitude.toDouble(), longitude.toDouble())
        else null
    }

    private fun initEmitters() {
        fetchPlacesListTriggerSubject = PublishSubject.create()
    }

    override fun onStop() {
        init = false
        discoverViewModel.unbind()
        super.onStop()
    }

    override fun fetchPlacesListTrigger(): Observable<FilteredFetchData> {
        return fetchPlacesListTriggerSubject
    }

    override fun render(discoverViewState: DiscoverViewState) {

    }
}