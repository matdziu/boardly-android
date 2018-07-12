package com.boardly.filter

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.constants.PICKED_FILTER
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICK_FILTER_REQUEST_CODE
import com.boardly.constants.PICK_GAME_REQUEST_CODE
import com.boardly.constants.SAVED_FILTER
import com.boardly.factories.FilterViewModelFactory
import com.boardly.filter.models.Filter
import com.boardly.injection.modules.GlideApp
import com.boardly.pickgame.PickGameActivity
import com.boardly.retrofit.gamesearch.models.SearchResult
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_filter.applyFilterButton
import kotlinx.android.synthetic.main.activity_filter.boardGameImageView
import kotlinx.android.synthetic.main.activity_filter.boardGameTextView
import kotlinx.android.synthetic.main.activity_filter.deleteGameButton
import kotlinx.android.synthetic.main.activity_filter.distanceSeekBar
import kotlinx.android.synthetic.main.activity_filter.distanceTextView
import kotlinx.android.synthetic.main.activity_filter.pickGameButton
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

    private fun initDistanceFilter(initialProgress: Int) {
        val seekBarMin = 1
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
        val actualInitialProgress = initialProgress - seekBarMin
        distanceSeekBar.progress = actualInitialProgress
    }

    private fun initGameFilter(gameName: String) {
        if (gameName.isNotEmpty()) boardGameTextView.text = gameName
    }

    override fun gameIdEmitter(): Observable<String> = gameIdSubject

    override fun render(filterViewState: FilterViewState) {
        loadImageFromUrl(boardGameImageView, filterViewState.gameImageUrl, R.drawable.board_game_placeholder)
    }
}