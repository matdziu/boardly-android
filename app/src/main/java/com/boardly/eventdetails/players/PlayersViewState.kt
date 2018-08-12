package com.boardly.eventdetails.players

import com.boardly.common.events.models.Player

data class PlayersViewState(val progress: Boolean = false,
                            val acceptedPlayersList: List<Player> = listOf())