package com.boardly.eventdetails.admin

import com.boardly.eventdetails.admin.network.AdminService
import io.reactivex.Observable
import javax.inject.Inject

class AdminInteractor @Inject constructor(private val adminService: AdminService) {

    fun fetchAcceptedPlayers(eventId: String): Observable<PartialAdminViewState> {
        return adminService.getAcceptedPlayers(eventId)
                .map { PartialAdminViewState.AcceptedPlayersFetched(it) }
    }

    fun fetchPendingPlayers(eventId: String): Observable<PartialAdminViewState> {
        return adminService.getPendingPlayers(eventId)
                .map { PartialAdminViewState.PendingPlayersFetched(it) }
    }
}