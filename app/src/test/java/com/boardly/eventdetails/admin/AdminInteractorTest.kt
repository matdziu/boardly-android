package com.boardly.eventdetails.admin

import com.boardly.base.rating.models.RateInput
import com.boardly.common.players.models.Player
import com.boardly.eventdetails.admin.network.AdminService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class AdminInteractorTest {

    private val testAcceptedPlayersList = listOf(Player(
            id = "acceptedTestId",
            rating = 5.0,
            ratedOrSelf = true))
    private val testPendingPlayersList = listOf(Player(
            id = "pendingTestId",
            rating = 5.0,
            ratedOrSelf = true))
    private val adminService: AdminService = mock {
        on { it.getAcceptedPlayers(any()) } doReturn Observable.just(testAcceptedPlayersList)
        on { it.getPendingPlayers(any()) } doReturn Observable.just(testPendingPlayersList)
        on { it.acceptPlayer(any(), any()) } doReturn Observable.just(true)
        on { it.kickPlayer(any(), any()) } doReturn Observable.just(true)
        on { it.sendRating(any()) } doReturn Observable.just(true)
    }

    private val adminInteractor = AdminInteractor(adminService)

    @Test
    fun testSuccessfulAcceptedPlayersFetching() {
        adminInteractor.fetchAcceptedPlayers("testEventId").test()
                .assertValue(PartialAdminViewState.AcceptedListState(testAcceptedPlayersList))
    }

    @Test
    fun testSuccessfulPendingPlayersFetching() {
        adminInteractor.fetchPendingPlayers("testEventId").test()
                .assertValue(PartialAdminViewState.PendingListState(testPendingPlayersList))
    }

    @Test
    fun testSuccessfulPlayerAccepting() {
        adminInteractor.acceptPlayer("testEventId", "testPlayerId").test()
                .assertValue { it is PartialAdminViewState.PlayerAccepted }
    }

    @Test
    fun testSuccessfulPlayerKicking() {
        adminInteractor.kickPlayer("testEventId", "testPlayerId").test()
                .assertValue { it is PartialAdminViewState.PlayerKicked }
    }

    @Test
    fun testSuccessfulRatingSending() {
        adminInteractor.sendRating(RateInput(5, "testPlayerId", "testEventId")).test()
                .assertValue { it is PartialAdminViewState.RatingSent }
    }
}