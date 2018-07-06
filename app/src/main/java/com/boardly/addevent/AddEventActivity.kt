package com.boardly.addevent

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.widget.Toast
import com.boardly.R
import com.boardly.addevent.dialogs.DatePickerFragment
import com.boardly.addevent.dialogs.TimePickerFragment
import com.boardly.base.BaseActivity
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICK_GAME_REQUEST_CODE
import com.boardly.constants.PLACE_AUTOCOMPLETE_REQUEST_CODE
import com.boardly.factories.AddEventViewModelFactory
import com.boardly.pickgame.PickGameActivity
import com.boardly.retrofit.gamesearch.models.SearchResult
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_add_event.boardGameImageView
import kotlinx.android.synthetic.main.activity_add_event.boardGameTextView
import kotlinx.android.synthetic.main.activity_add_event.levelTextView
import kotlinx.android.synthetic.main.activity_add_event.pickDateButton
import kotlinx.android.synthetic.main.activity_add_event.pickGameButton
import kotlinx.android.synthetic.main.activity_add_event.pickLevelButton
import kotlinx.android.synthetic.main.activity_add_event.pickPlaceButton
import kotlinx.android.synthetic.main.activity_add_event.placeTextView
import javax.inject.Inject


class AddEventActivity : BaseActivity(), AddEventView {

    @Inject
    lateinit var addEventViewModelFactory: AddEventViewModelFactory

    private lateinit var addEventViewModel: AddEventViewModel

    private lateinit var pickedGameIdSubject: PublishSubject<String>

    private val levelNames = listOf(
            R.string.beginner_level,
            R.string.intermediate_level,
            R.string.advanced_level)

    private val levelIdsMap = mapOf(
            R.string.beginner_level to "1",
            R.string.intermediate_level to "2",
            R.string.advanced_level to "3")

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
    }

    private fun initEmitters() {
        pickedGameIdSubject = PublishSubject.create()
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
            loadImageFromUrl(boardGameImageView, selectedGame.image, R.drawable.board_game_placeholder)
        }
    }

    private fun handleAutoCompleteResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val place = PlaceAutocomplete.getPlace(this, data)
                placeTextView.text = place.address
            }
            PlaceAutocomplete.RESULT_ERROR -> showErrorToast(R.string.generic_error)
            Activity.RESULT_CANCELED -> hideSoftKeyboard()
        }
    }

    private fun handlePickGameResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val pickedGame = data.getParcelableExtra<SearchResult>(PICKED_GAME)
                boardGameTextView.text = pickedGame.name
                pickedGameIdSubject.onNext(pickedGame.id.toString())
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
                    val clickedItemName = getString(levelNames[which])
                    levelTextView.text = clickedItemName
                })
                .create()
                .show()
    }

    private fun launchDatePickerDialog() {
        val datePickerDialog = DatePickerFragment()
        datePickerDialog.dateSetHandler = { year, month, dayOfMonth ->
            launchTimePickerDialog()
        }
        datePickerDialog.show(supportFragmentManager, "datePicker")
    }

    private fun launchTimePickerDialog() {
        val timePickerDialog = TimePickerFragment()
        timePickerDialog.show(supportFragmentManager, "timePicker")
    }

    private fun showErrorToast(@StringRes errorTextId: Int) {
        Toast.makeText(this, errorTextId, Toast.LENGTH_LONG).show()
    }

    override fun emitPickedGameId(): Observable<String> = pickedGameIdSubject
}