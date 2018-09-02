package com.boardly.eventdetails.players

import com.boardly.base.eventdetails.models.RateInput
import com.boardly.common.players.models.Player
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class PlayersViewModelTest {

    private val testAcceptedPlayersList = listOf(Player(
            id = "acceptedTestId",
            rating = 5.0))
    private val playersInteractor: PlayersInteractor = mock {
        on { it.fetchAcceptedPlayers("testEventId") } doReturn Observable.just(PartialPlayersViewState.AcceptedListState(testAcceptedPlayersList))
                .cast(PartialPlayersViewState::class.java)
        on { it.fetchAcceptedPlayers("testEventIdWithKicking") } doReturn Observable.just(PartialPlayersViewState.KickState())
                .cast(PartialPlayersViewState::class.java)
        on { it.sendRating(any()) } doReturn Observable.just(PartialPlayersViewState.RatingSent())
                .cast(PartialPlayersViewState::class.java)
    }

    @Test
    fun whenPlayersFetchTriggerIsEmittedAcceptedListIsFetched() {
        val playersViewRobot = PlayersViewRobot(PlayersViewModel(playersInteractor))
        playersViewRobot.triggerEventPlayersFetching("testEventId")
        playersViewRobot.assertViewStates(
                PlayersViewState(),
                PlayersViewState(progress = true),
                PlayersViewState(acceptedPlayersList = testAcceptedPlayersList))
    }

    @Test
    fun testSuccessfulRatingSending() {
        val modifiedAcceptedPlayersList = listOf(Player(
                id = "acceptedTestId",
                rating = 5.0,
                ratedOrSelf = true))
        val playersViewRobot = PlayersViewRobot(PlayersViewModel(playersInteractor, PlayersViewState(acceptedPlayersList = testAcceptedPlayersList)))
        playersViewRobot.emitRating(RateInput(5, "acceptedTestId", "testEventId"))
        playersViewRobot.assertViewStates(
                PlayersViewState(
                        acceptedPlayersList = testAcceptedPlayersList),
                PlayersViewState(
                        acceptedPlayersList = testAcceptedPlayersList),
                PlayersViewState(
                        acceptedPlayersList = modifiedAcceptedPlayersList)
        )
    }

    @Test
    fun testSuccessfulUserKicking() {
        val playersViewRobot = PlayersViewRobot(PlayersViewModel(playersInteractor))
        playersViewRobot.triggerEventPlayersFetching("testEventIdWithKicking")
        playersViewRobot.assertViewStates(
                PlayersViewState(),
                PlayersViewState(
                        progress = true),
                PlayersViewState(
                        kick = true))
    }
}