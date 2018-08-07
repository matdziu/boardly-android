package com.boardly.addevent

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.boardly.R
import com.boardly.addevent.dialogs.DatePickerFragment
import com.boardly.addevent.dialogs.TimePickerFragment
import com.boardly.base.BaseActivity
import com.boardly.constants.LEVEL_IDS_MAP
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICK_GAME_REQUEST_CODE
import com.boardly.constants.PLACE_AUTOCOMPLETE_REQUEST_CODE
import com.boardly.extensions.formatForDisplay
import com.boardly.factories.AddEventViewModelFactory
import com.boardly.pickgame.PickGameActivity
import com.boardly.retrofit.gamesearch.models.SearchResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_add_event.addEventButton
import kotlinx.android.synthetic.main.activity_add_event.boardGameImageView
import kotlinx.android.synthetic.main.activity_add_event.boardGameTextView
import kotlinx.android.synthetic.main.activity_add_event.contentViewGroup
import kotlinx.android.synthetic.main.activity_add_event.dateTextView
import kotlinx.android.synthetic.main.activity_add_event.descriptionEditText
import kotlinx.android.synthetic.main.activity_add_event.eventNameEditText
import kotlinx.android.synthetic.main.activity_add_event.levelTextView
import kotlinx.android.synthetic.main.activity_add_event.numberOfPlayersEditText
import kotlinx.android.synthetic.main.activity_add_event.pickDateButton
import kotlinx.android.synthetic.main.activity_add_event.pickGameButton
import kotlinx.android.synthetic.main.activity_add_event.pickLevelButton
import kotlinx.android.synthetic.main.activity_add_event.pickPlaceButton
import kotlinx.android.synthetic.main.activity_add_event.placeTextView
import kotlinx.android.synthetic.main.activity_add_event.progressBar
import java.util.*
import javax.inject.Inject


class AddEventActivity : BaseActivity(), AddEventView {

    @Inject
    lateinit var addEventViewModelFactory: AddEventViewModelFactory

    private lateinit var addEventViewModel: AddEventViewModel

    private lateinit var gamePickEventSubject: PublishSubject<String>
    private lateinit var placePickEventSubject: PublishSubject<Boolean>

    private val inputData = InputData()

    private var gamePickEvent = false

    private val levelNames = listOf(
            R.string.beginner_level,
            R.string.intermediate_level,
            R.string.advanced_level)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_add_event)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        addEventViewModel = ViewModelProviders.of(this, addEventViewModelFactory)[AddEventViewModel::class.java]

        pickGameButton.setOnClickListener { launchGamePickScreen() }
        pickPlaceButton.setOnClickListener { launchPlacePickScreen() }
        pickLevelButton.setOnClickListener { launchLevelDialog() }
        pickDateButton.setOnClickListener { launchDatePickerDialog() }
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        addEventViewModel.bind(this)

        if (gamePickEvent) gamePickEventSubject.onNext(inputData.gameId)
        gamePickEvent = false
    }

    private fun initEmitters() {
        gamePickEventSubject = PublishSubject.create()
        placePickEventSubject = PublishSubject.create()
    }

    override fun onStop() {
        addEventViewModel.unbind()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            when (requestCode) {
                PLACE_AUTOCOMPLETE_REQUEST_CODE -> handleAutoCompleteResult(resultCode, data)
                PICK_GAME_REQUEST_CODE -> handlePickGameResult(resultCode, data)
            }
        }
    }


    override fun render(addEventViewState: AddEventViewState) {
        with(addEventViewState) {
            inputData.gameImageUrl = selectedGame.image
            loadImageFromUrl(boardGameImageView, selectedGame.image, R.drawable.board_game_placeholder)
            eventNameEditText.showError(!eventNameValid)
            numberOfPlayersEditText.showError(!numberOfPlayersValid)
            showProgressBar(progress)
            showPickedGameError(!selectedGameValid)
            showPickedPlaceError(!selectedPlaceValid)
            if (success) {
                Toast.makeText(this@AddEventActivity, R.string.event_added_text, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun handleAutoCompleteResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val place = PlaceAutocomplete.getPlace(this, data)
                with(place) {
                    inputData.placeLatitude = latLng.latitude
                    inputData.placeLongitude = latLng.longitude
                    inputData.placeName = address.toString()
                    placeTextView.text = address

                    // Somehow default Places API Activity does not trigger onStop() of AddEventActivity
                    // so it's ok to emit event here
                    placePickEventSubject.onNext(true)
                }
            }
            PlaceAutocomplete.RESULT_ERROR -> showErrorToast(R.string.generic_error)
            Activity.RESULT_CANCELED -> hideSoftKeyboard()
        }
    }

    private fun handlePickGameResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val pickedGame = data.getParcelableExtra<SearchResult>(PICKED_GAME)
                with(pickedGame) {
                    boardGameTextView.text = name
                    inputData.gameId = id.toString()
                    inputData.gameName = name
                    gamePickEvent = true
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

    private fun launchGamePickScreen() {
        val pickGameIntent = Intent(this, PickGameActivity::class.java)
        startActivityForResult(pickGameIntent, PICK_GAME_REQUEST_CODE)
    }

    private fun launchLevelDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.pick_level_title)
                .setItems(levelNames.map { getString(it) }.toTypedArray(), { _, which ->
                    val itemNameResId = levelNames[which]
                    val clickedItemName = getString(itemNameResId)
                    inputData.levelId = LEVEL_IDS_MAP[itemNameResId].orEmpty()
                    levelTextView.text = clickedItemName
                })
                .create()
                .show()
    }

    private fun launchDatePickerDialog() {
        val datePickerDialog = DatePickerFragment()
        datePickerDialog.dateSetHandler = { year, month, dayOfMonth ->
            launchTimePickerDialog(year, month, dayOfMonth)
        }
        datePickerDialog.show(supportFragmentManager, "datePicker")
    }

    private fun launchTimePickerDialog(year: Int, month: Int, dayOfMonth: Int) {
        val timePickerDialog = TimePickerFragment()
        timePickerDialog.timeSetHandler = { hourOfDay, minute ->
            val pickedDate = getPickedDate(
                    year,
                    month,
                    dayOfMonth,
                    hourOfDay,
                    minute)
            inputData.timestamp = pickedDate.time
            dateTextView.text = pickedDate.formatForDisplay()
        }
        timePickerDialog.show(supportFragmentManager, "timePicker")
    }

    private fun getPickedDate(year: Int,
                              month: Int,
                              dayOfMonth: Int,
                              hourOfDay: Int,
                              minute: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth, hourOfDay, minute)
        return Date(calendar.timeInMillis)
    }

    private fun showErrorToast(@StringRes errorTextId: Int) {
        Toast.makeText(this, errorTextId, Toast.LENGTH_LONG).show()
    }

    override fun inputDataEmitter(): Observable<InputData> = RxView.clicks(addEventButton)
            .map {
                inputData.apply {
                    eventName = eventNameEditText.text.toString().trim()
                    maxPlayers = numberOfPlayersEditText.text.toString().toIntOrNull() ?: 0
                    description = descriptionEditText.text.toString().trim()
                }
            }

    override fun placePickEventEmitter(): Observable<Boolean> = placePickEventSubject

    override fun gamePickEventEmitter(): Observable<String> = gamePickEventSubject

    private fun showPickedGameError(show: Boolean) {
        if (show) {
            boardGameTextView.setTextColor(ContextCompat.getColor(this, R.color.errorRed))
        } else {
            boardGameTextView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
        }
    }

    private fun showPickedPlaceError(show: Boolean) {
        if (show) {
            placeTextView.setTextColor(ContextCompat.getColor(this, R.color.errorRed))
        } else {
            placeTextView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            contentViewGroup.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            contentViewGroup.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}