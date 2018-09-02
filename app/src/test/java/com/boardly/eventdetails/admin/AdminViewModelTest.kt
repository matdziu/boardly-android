package com.boardly.eventdetails.admin

import com.boardly.base.eventdetails.models.RateInput
import com.boardly.common.players.models.Player
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class AdminViewModelTest {

    private val testAcceptedPlayersList = listOf(Player(
            id = "acceptedTestId",
            rating = 5.0))
    private val testPendingPlayersList = listOf(Player(
            id = "pendingTestId",
            rating = 5.0))
    private val adminInteractor: AdminInteractor = mock {
        on { it.sendRating(any()) } doReturn Observable.just(PartialAdminViewState.RatingSent())
                .cast(PartialAdminViewState::class.java)
        on { it.kickPlayer(any(), any()) } doReturn Observable.just(PartialAdminViewState.PlayerKicked())
                .cast(PartialAdminViewState::class.java)
        on { it.acceptPlayer(any(), any()) } doReturn Observable.just(PartialAdminViewState.PlayerAccepted())
                .cast(PartialAdminViewState::class.java)
        on { it.fetchPendingPlayers(any()) } doReturn Observable.just(PartialAdminViewState.PendingListState(testPendingPlayersList))
                .cast(PartialAdminViewState::class.java)
        on { it.fetchAcceptedPlayers(any()) } doReturn Observable.just(PartialAdminViewState.AcceptedListState(testAcceptedPlayersList))
                .cast(PartialAdminViewState::class.java)
    }

    @Test
    fun whenPlayersFetchTriggerIsEmittedAcceptedAndPendingListsAreFetched() {
        val adminViewRobot = AdminViewRobot(AdminViewModel(adminInteractor))
        adminViewRobot.triggerEventPlayersFetching()
        adminViewRobot.assertViewStates(
                AdminViewState(),
                AdminViewState(
                        pendingProgress = true),
                AdminViewState(
                        pendingPlayersList = testPendingPlayersList),
                AdminViewState(
                        pendingPlayersList = testPendingPlayersList,
                        acceptedProgress = true),
                AdminViewState(
                        pendingPlayersList = testPendingPlayersList,
                        acceptedPlayersList = testAcceptedPlayersList))
    }

    @Test
    fun whenPlayerIsKickedPlayersListIsUpdated() {
        val adminViewRobot = AdminViewRobot(AdminViewModel(adminInteractor, AdminViewState(acceptedPlayersList = testAcceptedPlayersList)))
        adminViewRobot.kickPlayer("acceptedTestId")
        adminViewRobot.assertViewStates(
                AdminViewState(
                        acceptedPlayersList = testAcceptedPlayersList),
                AdminViewState(
                        acceptedPlayersList = testAcceptedPlayersList),
                AdminViewState())
    }

    @Test
    fun whenPlayerIsAcceptedPlayersListIsUpdated() {
        val adminViewRobot = AdminViewRobot(AdminViewModel(adminInteractor, AdminViewState(pendingPlayersList = testPendingPlayersList)))
        adminViewRobot.acceptPlayer("pendingTestId")
        adminViewRobot.assertViewStates(
                AdminViewState(
                        pendingPlayersList = testPendingPlayersList),
                AdminViewState(
                        pendingPlayersList = testPendingPlayersList),
                AdminViewState())
    }

    @Test
    fun testSuccessfulRatingSending() {
        val modifiedAcceptedPlayersList = listOf(Player(
                id = "acceptedTestId",
                rating = 5.0,
                ratedOrSelf = true))
        val adminViewRobot = AdminViewRobot(AdminViewModel(adminInteractor, AdminViewState(acceptedPlayersList = testAcceptedPlayersList)))
        adminViewRobot.emitRating(RateInput(5, "acceptedTestId", "testEventId"))
        adminViewRobot.assertViewStates(
                AdminViewState(
                        acceptedPlayersList = testAcceptedPlayersList),
                AdminViewState(
                        acceptedPlayersList = testAcceptedPlayersList),
                AdminViewState(
                        acceptedPlayersList = modifiedAcceptedPlayersList)
        )
    }
}