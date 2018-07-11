package com.boardly.filter

import android.os.Bundle
import android.widget.SeekBar
import com.boardly.R
import com.boardly.base.BaseActivity
import kotlinx.android.synthetic.main.activity_filter.distanceSeekBar
import kotlinx.android.synthetic.main.activity_filter.distanceTextView

class FilterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_filter)
        super.onCreate(savedInstanceState)
        showBackToolbarArrow(true, this::finish)
        initDistanceFilter(50)
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