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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.common.events.models.Event
import com.boardly.common.search.SearchResultData
import com.boardly.constants.EDIT_EVENT_REQUEST_CODE
import com.boardly.constants.EVENT
import com.boardly.constants.EVENT_EDITED_RESULT_CODE
import com.boardly.constants.EVENT_REMOVED_RESULT_CODE
import com.boardly.constants.LATITUDE
import com.boardly.constants.LONGITUDE
import com.boardly.constants.MODE
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICKED_SEARCH_RESULT
import com.boardly.constants.PICK_FIRST_GAME_REQUEST_CODE
import com.boardly.constants.PICK_SECOND_GAME_REQUEST_CODE
import com.boardly.constants.PICK_THIRD_GAME_REQUEST_CODE
import com.boardly.constants.PLACE_PICK_REQUEST_CODE
import com.boardly.constants.RPG_TYPE
import com.boardly.databinding.ActivityEventBinding
import com.boardly.event.dialogs.DatePickerFragment
import com.boardly.event.dialogs.TimePickerFragment
import com.boardly.event.models.GamePickEvent
import com.boardly.event.models.GamePickType
import com.boardly.event.models.Mode
import com.boardly.extensions.formatForDisplay
import com.boardly.extensions.loadImageFromUrl
import com.boardly.factories.EventViewModelFactory
import com.boardly.pickgame.PickGameActivity
import com.boardly.pickgame.dialog.addGameDialog
import com.boardly.pickplace.PickPlaceActivity
import com.boardly.retrofit.gameservice.models.Game
import com.boardly.retrofit.gameservice.models.SearchResult
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class EventActivity : BaseActivity(), EventView {

    @Inject
    lateinit var eventViewModelFactory: EventViewModelFactory

    private lateinit var eventViewModel: EventViewModel

    private lateinit var gamePickEventSubject: PublishSubject<GamePickEvent>
    private lateinit var placePickEventSubject: PublishSubject<Boolean>
    private lateinit var deleteEventSubject: PublishSubject<String>

    private val inputData = InputData()
    private var event = Event()

    private var emitGamePickEvent = false

    private var recentGamePickEvent = GamePickEvent()

    private lateinit var mode: Mode

    private lateinit var binding: ActivityEventBinding

    companion object {
        fun startAddMode(context: Context, event: Event? = null) {
            val intent = Intent(context, EventActivity::class.java)
            intent.putExtra(MODE, Mode.ADD)
            intent.putExtra(EVENT, event)
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
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)

        mode = intent.getSerializableExtra(MODE) as Mode
        prepareMode(mode)

        eventViewModel =
            ViewModelProviders.of(this, eventViewModelFactory)[EventViewModel::class.java]

        binding.pickGameButton.setOnClickListener {
            addGameDialog(binding.contentViewGroup) {
                handlePickGameResult(
                    GamePickType.FIRST,
                    it
                )
            }
        }
        binding.pickGameButton2.setOnClickListener {
            addGameDialog(binding.contentViewGroup) {
                handlePickGameResult(
                    GamePickType.SECOND,
                    it
                )
            }
        }
        binding.pickGameButton3.setOnClickListener {
            addGameDialog(binding.contentViewGroup) {
                handlePickGameResult(
                    GamePickType.THIRD,
                    it
                )
            }
        }
        binding.pickPlaceButton.setOnClickListener { launchPlacePickScreen() }
        binding.pickDateButton.setOnClickListener { launchDatePickerDialog() }

        binding.deleteEventButton.setOnClickListener { launchDeleteEventDialog() }
    }

    private fun launchDeleteEventDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_event_title))
            .setMessage(getString(R.string.are_you_sure_to_delete))
            .setPositiveButton(
                R.string.delete_event,
                { _, _ -> deleteEventSubject.onNext(event.eventId) })
            .setNegativeButton(R.string.dialog_cancel, null)
            .create()
            .show()
    }

    private fun prepareMode(mode: Mode) {
        if (mode == Mode.ADD) {
            binding.addEventButton.visibility = View.VISIBLE
            binding.saveChangesButton.visibility = View.GONE
            binding.deleteEventButton.visibility = View.GONE
            val partialEvent = intent.getParcelableExtra<Event>(EVENT)
            if (partialEvent != null) {
                event = partialEvent
                renderEventData(event)
                updateInputData(event)
            }
        } else if (mode == Mode.EDIT) {
            binding.addEventButton.visibility = View.GONE
            binding.saveChangesButton.visibility = View.VISIBLE
            binding.deleteEventButton.visibility = View.VISIBLE

            event = intent.getParcelableExtra(EVENT) ?: Event()
            renderEventData(event)
            updateInputData(event)
        }
    }

    private fun renderEventData(event: Event) {
        with(event) {
            binding.eventNameEditText.setText(eventName)
            binding.descriptionEditText.setText(description)
            loadGameSection(
                binding.boardGameImageView,
                gameImageUrl,
                binding.boardGameTextView,
                gameName
            )
            loadGameSection(
                binding.boardGameImageView2,
                gameImageUrl2,
                binding.boardGameTextView2,
                gameName2
            )
            loadGameSection(
                binding.boardGameImageView3,
                gameImageUrl3,
                binding.boardGameTextView3,
                gameName3
            )
            binding.placeTextView.text = placeName
            if (timestamp > 0) binding.dateTextView.text = Date(timestamp).formatForDisplay()
        }
    }

    private fun loadGameSection(
        gameImageView: ImageView, gameImageUrl: String,
        gameNameTextView: TextView, gameName: String
    ) {
        loadImageFromUrl(gameImageView, gameImageUrl, R.drawable.board_game_placeholder)
        gameNameTextView.text =
            if (gameName.isNotEmpty()) gameName else getString(R.string.game_text_placeholder)
    }

    private fun updateInputData(event: Event) {
        inputData.apply {
            eventId = event.eventId
            gameName = event.gameName
            gameId = event.gameId
            gameName2 = event.gameName2
            gameId2 = event.gameId2
            gameName3 = event.gameName3
            gameId3 = event.gameId3
            gameImageUrl = ""
            gameImageUrl2 = ""
            gameImageUrl3 = ""
            placeName = event.placeName
            placeLatitude = event.placeLatitude
            placeLongitude = event.placeLongitude
            timestamp = event.timestamp
            adminId = event.adminId
        }
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        eventViewModel.bind(this)

        if (emitGamePickEvent) gamePickEventSubject.onNext(recentGamePickEvent)
        emitGamePickEvent = false
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
                PLACE_PICK_REQUEST_CODE -> handlePlacePickResult(resultCode, data)
                PICK_FIRST_GAME_REQUEST_CODE -> handlePickGameResult(
                    resultCode,
                    data,
                    GamePickType.FIRST
                )

                PICK_SECOND_GAME_REQUEST_CODE -> handlePickGameResult(
                    resultCode,
                    data,
                    GamePickType.SECOND
                )

                PICK_THIRD_GAME_REQUEST_CODE -> handlePickGameResult(
                    resultCode,
                    data,
                    GamePickType.THIRD
                )
            }
        }
    }

    override fun render(eventViewState: EventViewState) {
        with(eventViewState) {
            binding.eventNameEditText.showError(!eventNameValid)
            showProgressBar(progress)
            showPickedGameError(!selectedGameValid)
            showPickedPlaceError(!selectedPlaceValid)
            if (success) {
                Toast.makeText(this@EventActivity, R.string.everything_went_ok, Toast.LENGTH_SHORT)
                    .show()
                setResult(EVENT_EDITED_RESULT_CODE)
                finish()
            }
            if (removed) {
                Toast.makeText(this@EventActivity, R.string.everything_went_ok, Toast.LENGTH_SHORT)
                    .show()
                setResult(EVENT_REMOVED_RESULT_CODE)
                finish()
            }

            loadAndSaveGameImage(
                selectedGame,
                { inputData.gameImageUrl = it },
                binding.boardGameImageView
            )
            loadAndSaveGameImage(
                selectedGame2,
                { inputData.gameImageUrl2 = it },
                binding.boardGameImageView2
            )
            loadAndSaveGameImage(
                selectedGame3,
                { inputData.gameImageUrl3 = it },
                binding.boardGameImageView3
            )
        }
    }

    private fun loadAndSaveGameImage(
        game: Game,
        inputDataSetter: (String) -> Unit,
        boardGameImageView: ImageView
    ) {
        if (game.id > 0) {
            inputDataSetter(game.image)
            loadImageFromUrl(boardGameImageView, game.image, R.drawable.board_game_placeholder)
        }
    }

    private fun handlePlacePickResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val place = data.getParcelableExtra<SearchResultData>(PICKED_SEARCH_RESULT)
                with(place) {
                    inputData.placeLatitude = this?.additionalInfo?.get(LATITUDE)?.toDouble() ?: 0.0
                    inputData.placeLongitude =
                        this?.additionalInfo?.get(LONGITUDE)?.toDouble() ?: 0.0
                    inputData.placeName = this?.title ?: ""
                    binding.placeTextView.text = title

                    // Somehow default Places API Activity does not trigger onStop() of EventActivity
                    // so it's ok to emit event here
                    placePickEventSubject.onNext(true)
                }
            }

            PlaceAutocomplete.RESULT_ERROR -> showErrorToast(R.string.generic_error)
            Activity.RESULT_CANCELED -> hideSoftKeyboard()
        }
    }

    private fun handlePickGameResult(resultCode: Int, data: Intent, gamePickType: GamePickType) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val pickedGame = data.getParcelableExtra<SearchResult>(PICKED_GAME)
                with(pickedGame) {
                    when (gamePickType) {
                        GamePickType.FIRST -> {
                            binding.boardGameTextView.text = this?.name
                            inputData.gameId = formatId(this?.id ?: 0, this?.type ?: "")
                            inputData.gameName = this?.name ?: ""
                        }

                        GamePickType.SECOND -> {
                            binding.boardGameTextView2.text = this?.name ?: ""
                            inputData.gameId2 = formatId(this?.id ?: 0, this?.type ?: "")
                            inputData.gameName2 = this?.name ?: ""
                        }

                        GamePickType.THIRD -> {
                            binding.boardGameTextView3.text = this?.name ?: ""
                            inputData.gameId3 = formatId(this?.id ?: 0, this?.type ?: "")
                            inputData.gameName3 = this?.name ?: ""
                        }
                    }
                    recentGamePickEvent =
                        GamePickEvent(formatId(this?.id ?: 0, this?.type ?: ""), gamePickType)
                    emitGamePickEvent = true
                }
            }
        }
    }

    private fun handlePickGameResult(gamePickType: GamePickType, gameName: String) {
        when (gamePickType) {
            GamePickType.FIRST -> {
                binding.boardGameTextView.text = gameName
                inputData.gameName = gameName
            }

            GamePickType.SECOND -> {
                binding.boardGameTextView2.text = gameName
                inputData.gameName2 = gameName
            }

            GamePickType.THIRD -> {
                binding.boardGameTextView3.text = gameName
                inputData.gameName3 = gameName
            }
        }
        gamePickEventSubject.onNext(recentGamePickEvent)
    }

    private fun formatId(id: Int, type: String): String =
        if (type == RPG_TYPE) "$id$RPG_TYPE" else id.toString()

    private fun launchPlacePickScreen() {
        startActivityForResult(Intent(this, PickPlaceActivity::class.java), PLACE_PICK_REQUEST_CODE)
    }

    private fun launchGamePickScreen(requestCode: Int) {
        val pickGameIntent = Intent(this, PickGameActivity::class.java)
        startActivityForResult(pickGameIntent, requestCode)
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
                minute
            )
            inputData.timestamp = pickedDate.time
            binding.dateTextView.text = pickedDate.formatForDisplay()
        }
        timePickerDialog.show(supportFragmentManager, "timePicker")
    }

    private fun getPickedDate(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hourOfDay: Int,
        minute: Int
    ): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth, hourOfDay, minute)
        return Date(calendar.timeInMillis)
    }

    private fun showErrorToast(@StringRes errorTextId: Int) {
        Toast.makeText(this, errorTextId, Toast.LENGTH_LONG).show()
    }

    override fun addEventEmitter(): Observable<InputData> = RxView.clicks(binding.addEventButton)
        .map {
            inputData.apply {
                eventName = binding.eventNameEditText.text.toString().trim()
                description = binding.descriptionEditText.text.toString().trim()
            }
        }

    override fun placePickEventEmitter(): Observable<Boolean> = placePickEventSubject

    override fun gamePickEventEmitter(): Observable<GamePickEvent> = gamePickEventSubject

    override fun editEventEmitter(): Observable<InputData> =
        RxView.clicks(binding.saveChangesButton)
            .map {
                inputData.apply {
                    eventName = binding.eventNameEditText.text.toString().trim()
                    description = binding.descriptionEditText.text.toString().trim()
                }
            }

    override fun deleteEventEmitter(): Observable<String> = deleteEventSubject

    private fun showPickedGameError(show: Boolean) {
        if (show) {
            binding.boardGameTextView.setTextColor(ContextCompat.getColor(this, R.color.errorRed))
        } else {
            binding.boardGameTextView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryText
                )
            )
        }
    }

    private fun showPickedPlaceError(show: Boolean) {
        if (show) {
            binding.placeTextView.setTextColor(ContextCompat.getColor(this, R.color.errorRed))
        } else {
            binding.placeTextView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryText
                )
            )
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            binding.contentViewGroup.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.contentViewGroup.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}