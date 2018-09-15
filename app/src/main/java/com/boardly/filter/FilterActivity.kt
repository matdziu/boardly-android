package com.boardly.filter

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.widget.SeekBar
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.common.location.UserLocation
import com.boardly.constants.PICKED_FILTER
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICK_FILTER_REQUEST_CODE
import com.boardly.constants.PICK_GAME_REQUEST_CODE
import com.boardly.constants.PLACE_AUTOCOMPLETE_REQUEST_CODE
import com.boardly.constants.SAVED_FILTER
import com.boardly.extensions.loadImageFromUrl
import com.boardly.factories.FilterViewModelFactory
import com.boardly.filter.models.Filter
import com.boardly.injection.modules.GlideApp
import com.boardly.pickgame.PickGameActivity
import com.boardly.retrofit.gamesearch.models.SearchResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_filter.applyFilterButton
import kotlinx.android.synthetic.main.activity_filter.boardGameImageView
import kotlinx.android.synthetic.main.activity_filter.boardGameTextView
import kotlinx.android.synthetic.main.activity_filter.deleteGameButton
import kotlinx.android.synthetic.main.activity_filter.distanceSeekBar
import kotlinx.android.synthetic.main.activity_filter.distanceTextView
import kotlinx.android.synthetic.main.activity_filter.locationTextView
import kotlinx.android.synthetic.main.activity_filter.pickGameButton
import kotlinx.android.synthetic.main.activity_filter.pickPlaceButton
import javax.inject.Inject

class FilterActivity : BaseActivity(), FilterView {

    private var currentFilter = Filter()

    private lateinit var gameIdSubject: PublishSubject<String>

    @Inject
    lateinit var filterViewModelFactory: FilterViewModelFactory

    private lateinit var filterViewModel: FilterViewModel

    private var fetchDetails = true

    companion object {
        fun start(activity: Activity, savedFilter: Filter) {
            val intent = Intent(activity, FilterActivity::class.java)
            intent.putExtra(SAVED_FILTER, savedFilter)
            activity.startActivityForResult(intent, PICK_FILTER_REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_filter)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        currentFilter = intent.getParcelableExtra(SAVED_FILTER)
        initDistanceFilter(currentFilter.radius.toInt())
        initGameFilter(currentFilter.gameName)
        initLocationFilter(currentFilter.locationName)

        filterViewModel = ViewModelProviders.of(this, filterViewModelFactory)[FilterViewModel::class.java]

        deleteGameButton.setOnClickListener {
            GlideApp.with(this).pauseAllRequests()
            boardGameTextView.text = getString(R.string.game_text_placeholder)
            boardGameImageView.setImageResource(R.drawable.board_game_placeholder)
            currentFilter.gameId = ""
            currentFilter.gameName = ""
            gameIdSubject.onNext("")
        }
        pickGameButton.setOnClickListener {
            launchGamePickScreen()
        }
        applyFilterButton.setOnClickListener {
            val data = Intent()
            data.putExtra(PICKED_FILTER, currentFilter)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
        pickPlaceButton.setOnClickListener { launchPlacePickScreen() }
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        filterViewModel.bind(this)
        if (fetchDetails) {
            fetchDetails = false
            gameIdSubject.onNext(currentFilter.gameId)
        }
    }

    private fun initEmitters() {
        gameIdSubject = PublishSubject.create()
    }

    override fun onStop() {
        filterViewModel.unbind()
        super.onStop()
    }

    private fun launchGamePickScreen() {
        val pickGameIntent = Intent(this, PickGameActivity::class.java)
        startActivityForResult(pickGameIntent, PICK_GAME_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            when (requestCode) {
                PICK_GAME_REQUEST_CODE -> handlePickGameResult(resultCode, data)
                PLACE_AUTOCOMPLETE_REQUEST_CODE -> handleAutoCompleteResult(resultCode, data)
            }
        }
    }

    private fun handlePickGameResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val pickedGame = data.getParcelableExtra<SearchResult>(PICKED_GAME)
                with(pickedGame) {
                    boardGameTextView.text = name
                    currentFilter.gameId = id.toString()
                    currentFilter.gameName = name
                    fetchDetails = true
                }
            }
        }
    }

    private fun launchPlacePickScreen() {
        try {
            val placeSearchIntent = PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this)
            startActivityForResult(placeSearchIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            showErrorToast(R.string.gps_update_needed)
        } catch (e: GooglePlayServicesNotAvailableException) {
            showErrorToast(R.string.gps_not_available)
        }
    }

    private fun handleAutoCompleteResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val place = PlaceAutocomplete.getPlace(this, data)
                with(place) {
                    val userLocation = UserLocation(latLng.latitude, latLng.longitude)
                    currentFilter.userLocation = userLocation
                    currentFilter.locationName = place.address.toString()
                    locationTextView.text = place.address
                }
            }
            PlaceAutocomplete.RESULT_ERROR -> showErrorToast(R.string.generic_error)
            Activity.RESULT_CANCELED -> hideSoftKeyboard()
        }
    }

    private fun showErrorToast(@StringRes errorTextId: Int) {
        Toast.makeText(this, errorTextId, Toast.LENGTH_LONG).show()
    }

    private fun initDistanceFilter(initialProgress: Int) {
        val seekBarMin = 1
        val actualInitialProgress = initialProgress - seekBarMin
        distanceTextView.text = getString(R.string.max_distance_text, initialProgress)
        distanceSeekBar.progress = actualInitialProgress
        distanceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val actualProgress = progress + seekBarMin
                currentFilter.radius = actualProgress.toDouble()
                distanceTextView.text = getString(R.string.max_distance_text, actualProgress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // unused
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // unused
            }
        })
    }

    private fun initGameFilter(gameName: String) {
        if (gameName.isNotEmpty()) boardGameTextView.text = gameName
    }

    private fun initLocationFilter(locationName: String) {
        if (locationName.isNotEmpty()) locationTextView.text = locationName
    }

    override fun gameIdEmitter(): Observable<String> = gameIdSubject

    override fun render(filterViewState: FilterViewState) {
        loadImageFromUrl(boardGameImageView, filterViewState.gameImageUrl, R.drawable.board_game_placeholder)
    }
}