package com.boardly.common.players

import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.boardly.R
import com.boardly.base.rating.RateView
import com.boardly.base.rating.models.RateInput
import com.boardly.common.players.models.Player
import kotlinx.android.synthetic.main.view_rate_dialog.view.ratingBar

class RatingPlayerUIRenderer(private val activity: AppCompatActivity) : PlayerUIRenderer(activity) {

    fun displayPlayerInfo(player: Player,
                          playerImageView: ImageView,
                          nameTextView: TextView,
                          helloTextView: TextView,
                          ratingTextView: TextView,
                          ratingImageView: ImageView,
                          rateButton: Button,
                          rateView: RateView) {
        super.displayPlayerInfo(player, playerImageView, nameTextView, helloTextView, ratingTextView, ratingImageView)
        displayRateButton(player.ratedOrSelf, rateButton)
        setRateButtonOnClick(player, rateButton, rateView)
    }

    private fun displayRateButton(ratedOrSelf: Boolean, rateButton: Button) {
        if (ratedOrSelf) rateButton.visibility = View.GONE
        else rateButton.visibility = View.VISIBLE
    }

    private fun setRateButtonOnClick(player: Player,
                                     rateButton: Button,
                                     rateView: RateView) {
        rateButton.setOnClickListener {
            val dialogView = LayoutInflater.from(rateButton.context)
                    .inflate(R.layout.view_rate_dialog, rateButton.parent as ViewGroup, false)

            val dialog = android.app.AlertDialog.Builder(activity)
                    .setTitle(R.string.rate_dialog_title)
                    .setPositiveButton(R.string.rate_dialog_positive_text) { _, _ ->
                        with(dialogView) {
                            rateView.emitRating(RateInput(ratingBar.rating.toInt(), player.id, player.eventId))
                        }
                    }
                    .setNegativeButton(R.string.rate_dialog_negative_text, null)
                    .setView(dialogView)
                    .create()

            dialog.show()
        }
    }
}