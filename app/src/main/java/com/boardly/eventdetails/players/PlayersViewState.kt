package com.boardly.eventdetails.players

import com.boardly.common.players.models.Player

data class PlayersViewState(val progress: Boolean = false,
                            val acceptedPlayersList: List<Player> = listOf(),
                            val kick: Boolean = false)