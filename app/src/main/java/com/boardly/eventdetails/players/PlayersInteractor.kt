package com.boardly.eventdetails.players

import com.boardly.eventdetails.players.network.PlayersService
import io.reactivex.Observable
import javax.inject.Inject

class PlayersInteractor @Inject constructor(private val playersService: PlayersService) {

    fun fetchAcceptedPlayers(eventId: String): Observable<PartialPlayersViewState> {
        return playersService.getAcceptedPlayers(eventId)
                .map { PartialPlayersViewState.AcceptedPlayersFetchedState(it) }
    }
}