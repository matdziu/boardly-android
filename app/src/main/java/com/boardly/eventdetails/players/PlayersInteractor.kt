package com.boardly.eventdetails.players

import com.boardly.base.rating.models.RateInput
import com.boardly.eventdetails.players.network.PlayersService
import io.reactivex.Observable
import javax.inject.Inject

class PlayersInteractor @Inject constructor(private val playersService: PlayersService) {

    fun fetchAcceptedPlayers(eventId: String): Observable<PartialPlayersViewState> {
        return playersService.getAcceptedPlayers(eventId)
                .map {
                    if (it.find { it.id == playersService.userId } == null) PartialPlayersViewState.KickState()
                    else PartialPlayersViewState.AcceptedListState(it)
                }
    }

    fun sendRating(rateInput: RateInput): Observable<PartialPlayersViewState> {
        return playersService.sendRating(rateInput)
                .map { PartialPlayersViewState.RatingSent() }
    }
}