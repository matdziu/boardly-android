package com.boardly.notify

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.common.location.UserLocation
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICK_NOTIFY_GAME_REQUEST_CODE
import com.boardly.constants.PLACE_AUTOCOMPLETE_REQUEST_CODE
import com.boardly.extensions.loadImageFromUrl
import com.boardly.factories.NotifyViewModelFactory
import com.boardly.notify.models.NotifySettings
import com.boardly.pickgame.PickGameActivity
import com.boardly.retrofit.gamesearch.models.SearchResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_notify.applyNotifySettingsButton
import kotlinx.android.synthetic.main.activity_notify.boardGameImageView
import kotlinx.android.synthetic.main.activity_notify.boardGameTextView
import kotlinx.android.synthetic.main.activity_notify.contentGroup
import kotlinx.android.synthetic.main.activity_notify.distanceSeekBar
import kotlinx.android.synthetic.main.activity_notify.distanceTextView
import kotlinx.android.synthetic.main.activity_notify.locationTextView
import kotlinx.android.synthetic.main.activity_notify.pickGameButton
import kotlinx.android.synthetic.main.activity_notify.pickPlaceButton
import kotlinx.android.synthetic.main.activity_notify.progressBar
import javax.inject.Inject


class NotifyActivity : BaseActivity(), NotifyView {

    private var currentSettings = NotifySettings()

    private lateinit var gameIdSubject: PublishSubject<String>

    private var fetchDetails = true

    @Inject
    lateinit var notifyViewModelFactory: NotifyViewModelFactory

    private lateinit var notifyViewModel: NotifyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_notify)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        initDistanceSetting(50)
        initGameSetting("")
        initLocationSetting("")

        pickGameButton.setOnClickListener { launchGamePickScreen() }
        pickPlaceButton.setOnClickListener { launchPlacePickScreen() }

        notifyViewModel = ViewModelProviders.of(this, notifyViewModelFactory)[NotifyViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        notifyViewModel.bind(this)
        if (fetchDetails) {
            fetchDetails = false
            gameIdSubject.onNext(currentSettings.gameId)
        }
    }

    private fun initEmitters() {
        gameIdSubject = PublishSubject.create()
    }

    override fun onStop() {
        notifyViewModel.unbind()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            when (requestCode) {
                PICK_NOTIFY_GAME_REQUEST_CODE -> handlePickGameResult(resultCode, data)
                PLACE_AUTOCOMPLETE_REQUEST_CODE -> handleAutoCompleteResult(resultCode, data)
            }
        }
    }

    override fun gameIdEmitter(): Observable<String> = gameIdSubject

    override fun notifySettingsEmitter(): Observable<NotifySettings> = RxView.clicks(applyNotifySettingsButton)
            .map { currentSettings }

    override fun render(notifyViewState: NotifyViewState) {
        with(notifyViewState) {
            loadImageFromUrl(boardGameImageView, gameImageUrl, com.boardly.R.drawable.board_game_placeholder)
            showProgressBar(progress)
            if (success) finish()
        }
    }

    private fun showProgressBar(progress: Boolean) {
        if (progress) {
            contentGroup.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            contentGroup.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    private fun handlePickGameResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val pickedGame = data.getParcelableExtra<SearchResult>(PICKED_GAME)
                with(pickedGame) {
                    boardGameTextView.text = name
                    currentSettings.gameId = id.toString()
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

    private fun showErrorToast(@StringRes errorTextId: Int) {
        Toast.makeText(this, errorTextId, Toast.LENGTH_LONG).show()
    }

    private fun handleAutoCompleteResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val place = PlaceAutocomplete.getPlace(this, data)
                with(place) {
                    currentSettings.userLocation = UserLocation(latLng.latitude, latLng.longitude)
                    locationTextView.text = place.address
                }
            }
            PlaceAutocomplete.RESULT_ERROR -> showErrorToast(R.string.generic_error)
            Activity.RESULT_CANCELED -> hideSoftKeyboard()
        }
    }

    private fun launchGamePickScreen() {
        val pickGameIntent = Intent(this, PickGameActivity::class.java)
        startActivityForResult(pickGameIntent, PICK_NOTIFY_GAME_REQUEST_CODE)
    }

    private fun initDistanceSetting(initialProgress: Int) {
        val seekBarMin = 1
        val actualInitialProgress = initialProgress - seekBarMin
        distanceTextView.text = getString(R.string.max_distance_text, initialProgress)
        distanceSeekBar.progress = actualInitialProgress
        distanceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val actualProgress = progress + seekBarMin
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

    private fun initGameSetting(gameName: String) {
        if (gameName.isNotEmpty()) boardGameTextView.text = gameName
    }

    private fun initLocationSetting(locationName: String) {
        if (locationName.isNotEmpty()) locationTextView.text = locationName
    }
}