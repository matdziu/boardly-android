package com.boardly.eventdetails.players

import com.boardly.common.events.models.Event
import com.boardly.common.players.models.Player

data class PlayersViewState(val playersProgress: Boolean = false,
                            val eventProgress: Boolean = false,
                            val acceptedPlayersList: List<Player> = listOf(),
                            val kick: Boolean = false,
                            val event: Event = Event())