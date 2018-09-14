package com.boardly.event

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.boardly.R
import com.boardly.analytics.Analytics
import com.boardly.base.BaseActivity
import com.boardly.common.events.models.Event
import com.boardly.constants.EDIT_EVENT_REQUEST_CODE
import com.boardly.constants.EVENT
import com.boardly.constants.EVENT_EDITED_RESULT_CODE
import com.boardly.constants.EVENT_REMOVED_RESULT_CODE
import com.boardly.constants.LEVEL_IDS_MAP
import com.boardly.constants.LEVEL_STRINGS_MAP
import com.boardly.constants.MODE
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICK_GAME_REQUEST_CODE
import com.boardly.constants.PLACE_AUTOCOMPLETE_REQUEST_CODE
import com.boardly.event.dialogs.DatePickerFragment
import com.boardly.event.dialogs.TimePickerFragment
import com.boardly.event.models.Mode
import com.boardly.extensions.formatForDisplay
import com.boardly.extensions.loadImageFromUrl
import com.boardly.factories.EventViewModelFactory
import com.boardly.pickgame.PickGameActivity
import com.boardly.retrofit.gamesearch.models.SearchResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_event.addEventButton
import kotlinx.android.synthetic.main.activity_event.boardGameImageView
import kotlinx.android.synthetic.main.activity_event.boardGameTextView
import kotlinx.android.synthetic.main.activity_event.contentViewGroup
import kotlinx.android.synthetic.main.activity_event.dateTextView
import kotlinx.android.synthetic.main.activity_event.deleteEventButton
import kotlinx.android.synthetic.main.activity_event.descriptionEditText
import kotlinx.android.synthetic.main.activity_event.eventNameEditText
import kotlinx.android.synthetic.main.activity_event.levelTextView
import kotlinx.android.synthetic.main.activity_event.pickDateButton
import kotlinx.android.synthetic.main.activity_event.pickGameButton
import kotlinx.android.synthetic.main.activity_event.pickLevelButton
import kotlinx.android.synthetic.main.activity_event.pickPlaceButton
import kotlinx.android.synthetic.main.activity_event.placeTextView
import kotlinx.android.synthetic.main.activity_event.progressBar
import kotlinx.android.synthetic.main.activity_event.saveChangesButton
import java.util.*
import javax.inject.Inject

class EventActivity : BaseActivity(), EventView {

    @Inject
    lateinit var eventViewModelFactory: EventViewModelFactory

    @Inject
    lateinit var analytics: Analytics

    private lateinit var eventViewModel: EventViewModel

    private lateinit var gamePickEventSubject: PublishSubject<String>
    private lateinit var placePickEventSubject: PublishSubject<Boolean>
    private lateinit var deleteEventSubject: PublishSubject<String>

    private val inputData = InputData()
    private var event = Event()

    private var gamePickEvent = false

    private val levelNames = listOf(
            R.string.beginner_level,
            R.string.intermediate_level,
            R.string.advanced_level)

    private lateinit var mode: Mode

    companion object {
        fun startAddMode(context: Context) {
            val intent = Intent(context, EventActivity::class.java)
            intent.putExtra(MODE, Mode.ADD)
            context.startActivity(intent)
        }

        fun startEditMode(fragment: Fragment, event: Event) {
            val intent = Intent(fragment.context, EventActivity::class.java)
            intent.putExtra(MODE, Mode.EDIT)
            intent.putExtra(EVENT, event)
            fragment.startActivityForResult(intent, EDIT_EVENT_REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_event)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        mode = intent.getSerializableExtra(MODE) as Mode
        prepareMode(mode)

        eventViewModel = ViewModelProviders.of(this, eventViewModelFactory)[EventViewModel::class.java]

        pickGameButton.setOnClickListener { launchGamePickScreen() }
        pickPlaceButton.setOnClickListener { launchPlacePickScreen() }
        pickLevelButton.setOnClickListener { launchLevelDialog() }
        pickDateButton.setOnClickListener { launchDatePickerDialog() }

        deleteEventButton.setOnClickListener { launchDeleteEventDialog() }
    }

    private fun launchDeleteEventDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_event_title))
                .setMessage(getString(R.string.are_you_sure_to_delete))
                .setPositiveButton(R.string.delete_event, { _, _ -> deleteEventSubject.onNext(event.eventId) })
                .setNegativeButton(R.string.dialog_cancel, null)
                .create()
                .show()
    }

    private fun prepareMode(mode: Mode) {
        if (mode == Mode.ADD) {
            addEventButton.visibility = View.VISIBLE
            saveChangesButton.visibility = View.GONE
            deleteEventButton.visibility = View.GONE
        } else if (mode == Mode.EDIT) {
            addEventButton.visibility = View.GONE
            saveChangesButton.visibility = View.VISIBLE
            deleteEventButton.visibility = View.VISIBLE

            event = intent.getParcelableExtra(EVENT)
            renderEventData(event)
            updateInputData(event)
        }
    }

    private fun renderEventData(event: Event) {
        with(event) {
            eventNameEditText.setText(eventName)
            descriptionEditText.setText(description)
            loadImageFromUrl(boardGameImageView, gameImageUrl, R.drawable.board_game_placeholder)
            boardGameTextView.text = gameName
            placeTextView.text = placeName
            LEVEL_STRINGS_MAP[levelId]?.let { levelTextView.text = getString(it) }
            if (timestamp > 0) dateTextView.text = Date(timestamp).formatForDisplay()
        }
    }

    private fun updateInputData(event: Event) {
        inputData.apply {
            eventId = event.eventId
            gameName = event.gameName
            gameId = event.gameId
            gameImageUrl = event.gameImageUrl
            placeName = event.placeName
            placeLatitude = event.placeLatitude
            placeLongitude = event.placeLongitude
            levelId = event.levelId
            timestamp = event.timestamp
            adminId = event.adminId
        }
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        eventViewModel.bind(this)

        if (gamePickEvent) gamePickEventSubject.onNext(inputData.gameId)
        gamePickEvent = false
    }

    private fun initEmitters() {
        gamePickEventSubject = PublishSubject.create()
        placePickEventSubject = PublishSubject.create()
        deleteEventSubject = PublishSubject.create()
    }

    override fun onStop() {
        eventViewModel.unbind()
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

    override fun render(eventViewState: EventViewState) {
        with(eventViewState) {
            eventNameEditText.showError(!eventNameValid)
            showProgressBar(progress)
            showPickedGameError(!selectedGameValid)
            showPickedPlaceError(!selectedPlaceValid)
            if (success) {
                if (mode == Mode.ADD) analytics.logEventAddedEvent()
                Toast.makeText(this@EventActivity, R.string.everything_went_ok, Toast.LENGTH_SHORT).show()
                setResult(EVENT_EDITED_RESULT_CODE)
                finish()
            }
            if (removed) {
                Toast.makeText(this@EventActivity, R.string.everything_went_ok, Toast.LENGTH_SHORT).show()
                setResult(EVENT_REMOVED_RESULT_CODE)
                finish()
            }
            if (selectedGame.id > 0) {
                inputData.gameImageUrl = selectedGame.image
                loadImageFromUrl(boardGameImageView, selectedGame.image, R.drawable.board_game_placeholder)
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

                    // Somehow default Places API Activity does not trigger onStop() of EventActivity
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

    override fun addEventEmitter(): Observable<InputData> = RxView.clicks(addEventButton)
            .map {
                inputData.apply {
                    eventName = eventNameEditText.text.toString().trim()
                    description = descriptionEditText.text.toString().trim()
                }
            }

    override fun placePickEventEmitter(): Observable<Boolean> = placePickEventSubject

    override fun gamePickEventEmitter(): Observable<String> = gamePickEventSubject

    override fun editEventEmitter(): Observable<InputData> = RxView.clicks(saveChangesButton)
            .map {
                inputData.apply {
                    eventName = eventNameEditText.text.toString().trim()
                    description = descriptionEditText.text.toString().trim()
                }
            }

    override fun deleteEventEmitter(): Observable<String> = deleteEventSubject

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