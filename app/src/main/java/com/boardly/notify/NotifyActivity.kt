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
import com.boardly.common.search.SearchResultData
import com.boardly.constants.LATITUDE
import com.boardly.constants.LONGITUDE
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICKED_SEARCH_RESULT
import com.boardly.constants.PICK_NOTIFY_GAME_REQUEST_CODE
import com.boardly.constants.PLACE_PICK_REQUEST_CODE
import com.boardly.constants.RPG_TYPE
import com.boardly.databinding.ActivityNotifyBinding
import com.boardly.extensions.loadImageFromUrl
import com.boardly.factories.NotifyViewModelFactory
import com.boardly.injection.modules.GlideApp
import com.boardly.notify.models.NotifySettings
import com.boardly.pickgame.PickGameActivity
import com.boardly.pickplace.PickPlaceActivity
import com.boardly.retrofit.gameservice.models.SearchResult
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
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

    private lateinit var binding: ActivityNotifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        binding = ActivityNotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        initWithNotifySettings(currentSettings)

        binding.pickGameButton.setOnClickListener { launchGamePickScreen() }
        binding.pickPlaceButton.setOnClickListener { launchPlacePickScreen() }

        binding.deleteGameButton.setOnClickListener {
            GlideApp.with(this).pauseAllRequests()
            binding.boardGameTextView.text = getString(R.string.game_text_placeholder)
            binding.boardGameImageView.setImageResource(R.drawable.board_game_placeholder)
            newSettings.gameId = ""
            newSettings.gameName = ""
            gameIdSubject.onNext("")
        }

        notifyViewModel =
            ViewModelProviders.of(this, notifyViewModelFactory)[NotifyViewModel::class.java]
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
                PLACE_PICK_REQUEST_CODE -> handlePlacePickResult(resultCode, data)
            }
        }
    }

    override fun gameIdEmitter(): Observable<String> = gameIdSubject

    override fun notifySettingsEmitter(): Observable<NotifySettings> =
        RxView.clicks(binding.applyNotifySettingsButton)
            .map { newSettings }

    override fun notifySettingsFetchEmitter(): Observable<Boolean> = notifySettingsFetchSubject

    override fun placePickEventEmitter(): Observable<Boolean> = placePickEventSubject

    override fun stopNotificationsButtonClickEmitter(): Observable<Boolean> =
        RxView.clicks(binding.deleteNotificationsButton)
            .map { true }

    override fun render(notifyViewState: NotifyViewState) {
        with(notifyViewState) {
            loadImageFromUrl(
                binding.boardGameImageView,
                gameImageUrl,
                com.boardly.R.drawable.board_game_placeholder
            )
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
            binding.locationTextView.setTextColor(ContextCompat.getColor(this, R.color.errorRed))
        } else {
            binding.locationTextView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryText
                )
            )
        }
    }

    private fun initWithNotifySettings(notifySettings: NotifySettings) {
        initDistanceSetting(notifySettings.radius.toInt())
        initGameSetting(notifySettings.gameName)
        initLocationSetting(notifySettings.locationName)
    }

    private fun showProgressBar(progress: Boolean) {
        if (progress) {
            binding.contentViewGroup.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.contentViewGroup.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun handlePickGameResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val pickedGame = data.getParcelableExtra<SearchResult>(PICKED_GAME)
                with(pickedGame) {
                    binding.boardGameTextView.text = this?.name
                    newSettings.gameId = if (this?.type == RPG_TYPE) "$id$RPG_TYPE" else this?.id.toString()
                    newSettings.gameName = this?.name ?: ""
                    fetchDetails = true
                }
            }
        }
    }

    private fun launchPlacePickScreen() {
        startActivityForResult(Intent(this, PickPlaceActivity::class.java), PLACE_PICK_REQUEST_CODE)
    }

    private fun showErrorToast(@StringRes errorTextId: Int) {
        Toast.makeText(this, errorTextId, Toast.LENGTH_LONG).show()
    }

    private fun handlePlacePickResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val place = data.getParcelableExtra<SearchResultData>(PICKED_SEARCH_RESULT)
                with(place) {
                    newSettings.userLatitude = this?.additionalInfo?.get(LATITUDE)?.toDouble() ?: 0.0
                    newSettings.userLongitude = this?.additionalInfo?.get(LONGITUDE)?.toDouble() ?: 0.0
                    newSettings.locationName = this?.title ?: ""
                    binding.locationTextView.text = title

                    // Somehow default Places API Activity does not trigger onStop() of EventActivity
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
        binding.distanceTextView.text = getString(R.string.max_distance_text, initialProgress)
        binding.distanceSeekBar.progress = actualInitialProgress
        binding.distanceSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val actualProgress = progress + seekBarMin
                newSettings.radius = actualProgress.toDouble()
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

    private fun initGameSetting(gameName: String) {
        if (gameName.isNotEmpty()) binding.boardGameTextView.text = gameName
    }

    private fun initLocationSetting(locationName: String) {
        if (locationName.isNotEmpty()) binding.locationTextView.text = locationName
    }
}