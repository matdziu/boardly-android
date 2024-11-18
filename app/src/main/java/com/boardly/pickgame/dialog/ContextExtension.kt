package com.boardly.pickgame.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.boardly.R
import com.boardly.customviews.BoardlyEditText

fun Context.addGameDialog(parent: ViewGroup, onGameAdded: (String) -> Unit) {
    val dialogView = LayoutInflater.from(this)
        .inflate(R.layout.view_add_game, parent, false)

    val dialog = AlertDialog.Builder(this)
        .setTitle(R.string.add_game_dialog_title)
        .setPositiveButton(R.string.add_game_dialog_positive_text, null)
        .setNegativeButton(R.string.add_game_dialog_negative_text, null)
        .setView(dialogView)
        .create()

    dialog.show()

    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
        with(dialogView) {
            val gameNameEditText = this.findViewById<BoardlyEditText>(R.id.gameNameEditText)
            val gameName = gameNameEditText.text.trim().toString()
            if (gameName.isNotEmpty()) {
                onGameAdded(gameName)
                dialog.cancel()
                hideSoftKeyboard()
            } else {
                gameNameEditText.showError(true)
            }
        }
    }
    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
        dialog.cancel()
        hideSoftKeyboard()
    }
}

private fun Context.hideSoftKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}