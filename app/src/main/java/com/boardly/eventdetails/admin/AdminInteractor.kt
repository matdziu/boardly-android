package com.boardly.eventdetails.admin

import com.boardly.base.rating.models.RateInput
import com.boardly.eventdetails.admin.network.AdminService
import io.reactivex.Observable
import javax.inject.Inject

class AdminInteractor @Inject constructor(private val adminService: AdminService) {

    fun fetchAcceptedPlayers(eventId: String): Observable<PartialAdminViewState> {
        return adminService.getAcceptedPlayers(eventId)
                .map { PartialAdminViewState.AcceptedListState(it) }
    }

    fun fetchPendingPlayers(eventId: String): Observable<PartialAdminViewState> {
        return adminService.getPendingPlayers(eventId)
                .map { PartialAdminViewState.PendingListState(it) }
    }

    fun acceptPlayer(eventId: String, playerId: String): Observable<PartialAdminViewState> {
        return adminService.acceptPlayer(eventId, playerId)
                .map { PartialAdminViewState.PlayerAccepted() }
    }

    fun kickPlayer(eventId: String, playerId: String): Observable<PartialAdminViewState> {
        return adminService.kickPlayer(eventId, playerId)
                .map { PartialAdminViewState.PlayerKicked() }
    }

    fun sendRating(rateInput: RateInput): Observable<PartialAdminViewState> {
        return adminService.sendRating(rateInput)
                .map { PartialAdminViewState.RatingSent() }
    }
}