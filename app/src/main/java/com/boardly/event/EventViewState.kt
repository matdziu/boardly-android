package com.boardly.event

import com.boardly.retrofit.gamesearch.models.Game

data class EventViewState(val progress: Boolean = false,
                          val success: Boolean = false,
                          val selectedGame: Game = Game(),
                          val eventNameValid: Boolean = true,
                          val selectedGameValid: Boolean = true,
                          val selectedPlaceValid: Boolean = true)