package com.boardly.extensions

import android.view.View
import com.boardly.common.players.models.Player

fun List<Player>.mapToRatedPlayerCopy(ratedPredicate: (Player) -> Boolean): List<Player> {
    return map { if (ratedPredicate(it)) it.copy(ratedOrSelf = true) else it.copy() }
}

fun List<View>.setOnClickListener(clickAction: () -> Unit) {
    for (view in this) {
        if (view.visibility == View.VISIBLE) view.setOnClickListener { clickAction() }
    }
}