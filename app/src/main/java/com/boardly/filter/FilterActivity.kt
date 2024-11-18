package com.boardly.filter

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.annotation.StringRes
import android.widget.SeekBar
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.common.location.UserLocation
import com.boardly.common.search.SearchResultData
import com.boardly.constants.LATITUDE
import com.boardly.constants.LONGITUDE
import com.boardly.constants.PICKED_FILTER
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICKED_SEARCH_RESULT
import com.boardly.constants.PICK_FILTER_REQUEST_CODE
import com.boardly.constants.PICK_FIRST_GAME_REQUEST_CODE
import com.boardly.constants.PLACE_PICK_REQUEST_CODE
import com.boardly.constants.RPG_TYPE
import com.boardly.constants.SAVED_FILTER
import com.boardly.databinding.ActivityFilterBinding
import com.boardly.extensions.loadImageFromUrl
import com.boardly.factories.FilterViewModelFactory
import com.boardly.filter.models.Filter
import com.boardly.injection.modules.GlideApp
import com.boardly.pickgame.PickGameActivity
import com.boardly.pickplace.PickPlaceActivity
import com.boardly.retrofit.gameservice.models.SearchResult
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class FilterActivity : BaseActivity(), FilterView {

    private var currentFilter = Filter()

    private lateinit var gameIdSubject: PublishSubject<String>
    private lateinit var locationProcessingSubject: PublishSubject<Boolean>

    @Inject
    lateinit var filterViewModelFactory: FilterViewModelFactory

    private lateinit var filterViewModel: FilterViewModel

    private var fetchDetails = true

    private lateinit var binding: ActivityFilterBinding

    companion object {
        fun start(activity: Activity, savedFilter: Filter) {
            val intent = Intent(activity, FilterActivity::class.java)
            intent.putExtra(SAVED_FILTER, savedFilter)
            activity.startActivityForResult(intent, PICK_FILTER_REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        currentFilter = intent.getParcelableExtra(SAVED_FILTER) ?: Filter()
        initDistanceFilter(currentFilter.radius.toInt())
        initGameFilter(currentFilter.gameName)
        initLocationFilter(currentFilter.locationName)

        filterViewModel =
            ViewModelProviders.of(this, filterViewModelFactory)[FilterViewModel::class.java]

        binding.deleteGameButton.setOnClickListener {
            GlideApp.with(this).pauseAllRequests()
            binding.boardGameTextView.text = getString(R.string.game_text_placeholder)
            binding.boardGameImageView.setImageResource(R.drawable.board_game_placeholder)
            currentFilter.gameId = ""
            currentFilter.gameName = ""
            gameIdSubject.onNext("")
        }
        binding.pickGameButton.setOnClickListener {
            launchGamePickScreen()
        }
        binding.applyFilterButton.setOnClickListener {
            val data = Intent()
            data.putExtra(PICKED_FILTER, currentFilter)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
        binding.pickPlaceButton.setOnClickListener { launchPlacePickScreen() }
        binding.currentLocationButton.setOnClickListener {
            checkLocationSettings({
                locationProcessingSubject.onNext(true)
                val onLocationFound = { location: Location ->
                    currentFilter.userLocation = UserLocation(location.latitude, location.longitude)
                    currentFilter.locationName = getString(R.string.current_location_info)
                    currentFilter.isCurrentLocation = true
                    binding.locationTextView.text = getString(R.string.current_location_info)
                    locationProcessingSubject.onNext(false)
                }
                getLastKnownLocation(true) { onLocationFound(it) }
            })
        }
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
        locationProcessingSubject = PublishSubject.create()
    }

    override fun onStop() {
        filterViewModel.unbind()
        super.onStop()
    }

    private fun launchGamePickScreen() {
        val pickGameIntent = Intent(this, PickGameActivity::class.java)
        startActivityForResult(pickGameIntent, PICK_FIRST_GAME_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            when (requestCode) {
                PICK_FIRST_GAME_REQUEST_CODE -> handlePickGameResult(resultCode, data)
                PLACE_PICK_REQUEST_CODE -> handlePlacePickResult(resultCode, data)
            }
        }
    }

    private fun handlePickGameResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val pickedGame = data.getParcelableExtra<SearchResult>(PICKED_GAME)
                with(pickedGame) {
                    binding.boardGameTextView.text = this?.name ?: ""
                    currentFilter.gameId = if (this?.type == RPG_TYPE) "$id$RPG_TYPE" else this?.id.toString()
                    currentFilter.gameName = this?.name ?: ""
                    fetchDetails = true
                }
            }
        }
    }

    private fun launchPlacePickScreen() {
        startActivityForResult(Intent(this, PickPlaceActivity::class.java), PLACE_PICK_REQUEST_CODE)
    }

    private fun handlePlacePickResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val place = data.getParcelableExtra<SearchResultData>(PICKED_SEARCH_RESULT)
                with(place) {
                    val userLocation = UserLocation(
                        this?.additionalInfo?.get(LATITUDE)?.toDouble() ?: 0.0,
                        this?.additionalInfo?.get(LONGITUDE)?.toDouble() ?: 0.0
                    )
                    currentFilter.userLocation = userLocation
                    currentFilter.locationName = place?.title ?: ""
                    currentFilter.isCurrentLocation = false
                    binding.locationTextView.text = place?.title ?: ""
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
        binding.distanceTextView.text = getString(R.string.max_distance_text, initialProgress)
        binding.distanceSeekBar.progress = actualInitialProgress
        binding.distanceSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val actualProgress = progress + seekBarMin
                currentFilter.radius = actualProgress.toDouble()
                binding.distanceTextView.text =
                    getString(R.string.max_distance_text, actualProgress)
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
        if (gameName.isNotEmpty()) binding.boardGameTextView.text = gameName
    }

    private fun initLocationFilter(locationName: String) {
        if (locationName.isNotEmpty()) binding.locationTextView.text = locationName
    }

    override fun gameIdEmitter(): Observable<String> = gameIdSubject

    override fun locationProcessingEmitter(): Observable<Boolean> = locationProcessingSubject

    override fun render(filterViewState: FilterViewState) {
        with(filterViewState) {
            loadImageFromUrl(
                binding.boardGameImageView,
                gameImageUrl,
                R.drawable.board_game_placeholder
            )
            if (locationProcessing) {
                binding.locationTextView.text = getString(R.string.location_processing_text)
                binding.pickPlaceButton.isClickable = false
            } else {
                binding.pickPlaceButton.isClickable = true
            }
        }
    }
}