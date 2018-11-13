package com.boardly.notify

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICK_NOTIFY_GAME_REQUEST_CODE
import com.boardly.constants.PLACE_AUTOCOMPLETE_REQUEST_CODE
import com.boardly.constants.RPG_TYPE
import com.boardly.extensions.loadImageFromUrl
import com.boardly.factories.NotifyViewModelFactory
import com.boardly.injection.modules.GlideApp
import com.boardly.notify.models.NotifySettings
import com.boardly.pickgame.PickGameActivity
import com.boardly.retrofit.gameservice.models.SearchResult
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
import kotlinx.android.synthetic.main.activity_notify.contentViewGroup
import kotlinx.android.synthetic.main.activity_notify.deleteGameButton
import kotlinx.android.synthetic.main.activity_notify.deleteNotificationsButton
import kotlinx.android.synthetic.main.activity_notify.distanceSeekBar
import kotlinx.android.synthetic.main.activity_notify.distanceTextView
import kotlinx.android.synthetic.main.activity_notify.locationTextView
import kotlinx.android.synthetic.main.activity_notify.pickGameButton
import kotlinx.android.synthetic.main.activity_notify.pickPlaceButton
import kotlinx.android.synthetic.main.activity_notify.progressBar
import javax.inject.Inject


class NotifyActivity : BaseActivity(), NotifyView {

    private var newSettings = NotifySettings()
    private var currentSettings = NotifySettings()

    private lateinit var gameIdSubject: PublishSubject<String>
    private lateinit var notifySettingsFetchSubject: PublishSubject<Boolean>
    private lateinit var placePickEventSubject: PublishSubject<Boolean>

    private var fetchDetails = true
    private var init = true

    @Inject
    lateinit var notifyViewModelFactory: NotifyViewModelFactory

    private lateinit var notifyViewModel: NotifyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_notify)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        initWithNotifySettings(currentSettings)

        pickGameButton.setOnClickListener { launchGamePickScreen() }
        pickPlaceButton.setOnClickListener { launchPlacePickScreen() }

        deleteGameButton.setOnClickListener {
            GlideApp.with(this).pauseAllRequests()
            boardGameTextView.text = getString(R.string.game_text_placeholder)
            boardGameImageView.setImageResource(R.drawable.board_game_placeholder)
            newSettings.gameId = ""
            newSettings.gameName = ""
            gameIdSubject.onNext("")
        }

        notifyViewModel = ViewModelProviders.of(this, notifyViewModelFactory)[NotifyViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        notifyViewModel.bind(this)
        notifySettingsFetchSubject.onNext(init)
        if (fetchDetails) {
            fetchDetails = false
            gameIdSubject.onNext(newSettings.gameId)
        }
    }

    private fun initEmitters() {
        gameIdSubject = PublishSubject.create()
        notifySettingsFetchSubject = PublishSubject.create()
        placePickEventSubject = PublishSubject.create()
    }

    override fun onStop() {
        init = false
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
            .map { newSettings }

    override fun notifySettingsFetchEmitter(): Observable<Boolean> = notifySettingsFetchSubject

    override fun placePickEventEmitter(): Observable<Boolean> = placePickEventSubject

    override fun stopNotificationsButtonClickEmitter(): Observable<Boolean> = RxView.clicks(deleteNotificationsButton)
            .map { true }

    override fun render(notifyViewState: NotifyViewState) {
        with(notifyViewState) {
            loadImageFromUrl(boardGameImageView, gameImageUrl, com.boardly.R.drawable.board_game_placeholder)
            showProgressBar(progress)
            showPlacePickedError(!selectedPlaceValid)
            if (success) finish()
            if (currentSettings != notifySettings) {
                currentSettings = notifySettings
                newSettings = notifySettings
                initWithNotifySettings(notifySettings)
                gameIdSubject.onNext(notifySettings.gameId)
            }
        }
    }

    private fun showPlacePickedError(show: Boolean) {
        if (show) {
            locationTextView.setTextColor(ContextCompat.getColor(this, R.color.errorRed))
        } else {
            locationTextView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
        }
    }

    private fun initWithNotifySettings(notifySettings: NotifySettings) {
        initDistanceSetting(notifySettings.radius.toInt())
        initGameSetting(notifySettings.gameName)
        initLocationSetting(notifySettings.locationName)
    }

    private fun showProgressBar(progress: Boolean) {
        if (progress) {
            contentViewGroup.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            contentViewGroup.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    private fun handlePickGameResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val pickedGame = data.getParcelableExtra<SearchResult>(PICKED_GAME)
                with(pickedGame) {
                    boardGameTextView.text = name
                    newSettings.gameId = if (type == RPG_TYPE) "$id$RPG_TYPE" else id.toString()
                    newSettings.gameName = name
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
                    newSettings.userLatitude = latLng.latitude
                    newSettings.userLongitude = latLng.longitude
                    newSettings.locationName = place.address.toString()
                    locationTextView.text = place.address

                    // Somehow default Places API Activity does not trigger onStop() of NotifyActivity
                    // so it's ok to emit event here
                    placePickEventSubject.onNext(true)
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
                newSettings.radius = actualProgress.toDouble()
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