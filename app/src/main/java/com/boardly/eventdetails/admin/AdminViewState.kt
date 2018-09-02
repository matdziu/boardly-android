package com.boardly.eventdetails.admin

import com.boardly.common.events.models.Event
import com.boardly.common.players.models.Player

data class AdminViewState(val acceptedProgress: Boolean = false,
                          val pendingProgress: Boolean = false,
                          val eventProgress: Boolean = false,
                          val acceptedPlayersList: List<Player> = listOf(),
                          val pendingPlayersList: List<Player> = listOf(),
                          val event: Event = Event())