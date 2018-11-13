package com.boardly.event

import com.boardly.retrofit.gameservice.models.Game

data class EventViewState(val progress: Boolean = false,
                          val success: Boolean = false,
                          val removed: Boolean = false,
                          val selectedGame: Game = Game(),
                          val selectedGame2: Game = Game(),
                          val selectedGame3: Game = Game(),
                          val eventNameValid: Boolean = true,
                          val selectedGameValid: Boolean = true,
                          val selectedPlaceValid: Boolean = true)