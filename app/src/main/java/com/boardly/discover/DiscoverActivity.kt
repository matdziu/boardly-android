package com.boardly.discover

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
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
import kotlinx.android.synthetic.main.activity_discover.contentView
import kotlinx.android.synthetic.main.activity_discover.noPlacesTextView
import kotlinx.android.synthetic.main.activity_discover.noUserLocationTextView
import kotlinx.android.synthetic.main.activity_discover.progressBar
import javax.inject.Inject

class DiscoverActivity : BaseActivity(), DiscoverView {

    @Inject
    lateinit var discoverViewModelFactory: DiscoverViewModelFactory

    private lateinit var discoverViewModel: DiscoverViewModel

    private lateinit var fetchPlacesListTriggerSubject: PublishSubject<FilteredFetchData>

    private var init = true
    private var currentFilteredFetchData: FilteredFetchData? = null

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
        currentFilteredFetchData = getFilteredFetchData()
    }

    private fun getFilteredFetchData(): FilteredFetchData? {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val savedRadius = sharedPrefs.getInt(SAVED_RADIUS, 50)
        val savedLocationLongitude = sharedPrefs.getString(SAVED_LOCATION_LONGITUDE, "")
        val savedLocationLatitude = sharedPrefs.getString(SAVED_LOCATION_LATITUDE, "")
        val userLocation = getUserLocationFromString(savedLocationLongitude, savedLocationLatitude)

        return if (userLocation != null) FilteredFetchData(userLocation, savedRadius.toDouble())
        else null
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        discoverViewModel.bind(this)
        if (init && currentFilteredFetchData != null) fetchPlacesListTriggerSubject.onNext(currentFilteredFetchData!!)
    }

    private fun getUserLocationFromString(longitude: String, latitude: String): UserLocation? {
        return if (longitude != "null" && latitude != "null" &&
                longitude.isNotEmpty() && latitude.isNotEmpty()) UserLocation(latitude.toDouble(), longitude.toDouble())
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

    override fun render(discoverViewState: DiscoverViewState) = with(discoverViewState) {
        if (currentFilteredFetchData != null) {
            showProgressBar(progress)
            showNoPlacesText(placesList.isEmpty())
            if (placesList.isNotEmpty()) {
                showNoPlacesText(false)
                showNoLocationText(false)
            }
        } else {
            showNoLocationText(true)
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            contentView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            contentView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    private fun showNoLocationText(show: Boolean) {
        if (show) {
            noUserLocationTextView.visibility = View.VISIBLE
            noPlacesTextView.visibility = View.GONE
        } else {
            noUserLocationTextView.visibility = View.GONE
        }
    }

    private fun showNoPlacesText(show: Boolean) {
        if (show) {
            noPlacesTextView.visibility = View.VISIBLE
            noUserLocationTextView.visibility = View.GONE
        } else {
            noPlacesTextView.visibility = View.GONE
        }
    }
}