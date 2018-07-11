package com.boardly.filter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import com.boardly.R
import com.boardly.base.BaseActivity
import com.boardly.constants.PICKED_GAME
import com.boardly.constants.PICK_GAME_REQUEST_CODE
import com.boardly.pickgame.PickGameActivity
import com.boardly.retrofit.gamesearch.models.SearchResult
import kotlinx.android.synthetic.main.activity_filter.boardGameTextView
import kotlinx.android.synthetic.main.activity_filter.distanceSeekBar
import kotlinx.android.synthetic.main.activity_filter.distanceTextView
import kotlinx.android.synthetic.main.activity_filter.pickGameButton

class FilterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_filter)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
        initDistanceFilter(50)

        pickGameButton.setOnClickListener { launchGamePickScreen() }
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
                }
            }
        }
    }

    private fun initDistanceFilter(initialProgress: Int) {
        distanceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                distanceTextView.text = getString(R.string.max_distance_text, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // unused
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // unused
            }
        })
        distanceSeekBar.progress = initialProgress
    }
}