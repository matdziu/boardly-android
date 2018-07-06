package com.boardly.addevent

import com.boardly.retrofit.gamesearch.models.Game

data class AddEventViewState(val progress: Boolean = false,
                             val selectedGame: Game = Game(),
                             val eventNameValid: Boolean = true,
                             val numberOfPlayersValid: Boolean = true,
                             val selectedGameValid: Boolean = true,
                             val selectedPlaceValid: Boolean = true)