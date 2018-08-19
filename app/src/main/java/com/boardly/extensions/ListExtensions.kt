package com.boardly.extensions

import com.boardly.common.players.models.Player

fun List<Player>.mapToRatedPlayerCopy(ratedPredicate: (Player) -> Boolean): List<Player> {
    return map { if (ratedPredicate(it)) it.copy(ratedOrSelf = true) else it.copy() }
}