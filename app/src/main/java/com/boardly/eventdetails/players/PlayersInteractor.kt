package com.boardly.eventdetails.players

import com.boardly.base.eventdetails.models.RateInput
import com.boardly.eventdetails.players.network.PlayersService
import io.reactivex.Observable
import javax.inject.Inject

class PlayersInteractor @Inject constructor(private val playersService: PlayersService) {

    fun fetchAcceptedPlayers(eventId: String): Observable<PartialPlayersViewState> {
        return playersService.getAcceptedPlayers(eventId)
                .map {
                    if (it.find { it.id == playersService.userId } == null) PartialPlayersViewState.KickedState()
                    else PartialPlayersViewState.AcceptedListState(it)
                }
    }

    fun sendRating(rateInput: RateInput): Observable<PartialPlayersViewState> {
        return playersService.sendRating(rateInput)
                .map { PartialPlayersViewState.RatingSent() }
    }

    fun fetchEvent(eventId: String): Observable<PartialPlayersViewState> {
        return playersService.fetchEventDetails(eventId)
                .map { PartialPlayersViewState.EventFetched(it) }
    }

    fun leaveEvent(eventId: String): Observable<PartialPlayersViewState> {
        return playersService.leaveEvent(eventId)
                .map { PartialPlayersViewState.LeftEventState() }
    }
}