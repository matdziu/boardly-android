package com.boardly.eventdetails.players

import com.boardly.base.eventdetails.models.RateInput
import com.boardly.common.events.models.Event
import com.boardly.common.players.models.Player
import com.boardly.eventdetails.players.network.PlayersService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class PlayersInteractorTest {

    private val testAcceptedPlayersList1 = listOf(Player(
            id = "acceptedTestId",
            rating = 5.0,
            ratedOrSelf = true))
    private val testAcceptedPlayersList2 = listOf(Player(
            id = "test",
            rating = 5.0,
            ratedOrSelf = true))
    private val testEvent = Event("testEventId", "testEventName", "testGameId")
    private val playersService: PlayersService = mock {
        on { it.getAcceptedPlayers("testEventId1") } doReturn Observable.just(testAcceptedPlayersList1)
        on { it.getAcceptedPlayers("testEventId2") } doReturn Observable.just(testAcceptedPlayersList2)
        on { it.sendRating(any()) } doReturn Observable.just(true)
        on { it.userId } doReturn "acceptedTestId"
        on { it.fetchEventDetails(any()) } doReturn Observable.just(testEvent)
    }
    private val playersInteractor = PlayersInteractor(playersService)

    @Test
    fun testSuccessfulAcceptedPlayersFetching() {
        playersInteractor.fetchAcceptedPlayers("testEventId1").test()
                .assertValue(PartialPlayersViewState.AcceptedListState(testAcceptedPlayersList1))
    }

    @Test
    fun testSuccessfulPlayerKick() {
        playersInteractor.fetchAcceptedPlayers("testEventId2").test()
                .assertValue { it is PartialPlayersViewState.KickState }
    }

    @Test
    fun testSuccessfulRatingSending() {
        playersInteractor.sendRating(RateInput(5, "testPlayerId", "testEventId")).test()
                .assertValue { it is PartialPlayersViewState.RatingSent }
    }

    @Test
    fun testSuccessfulEventFetching() {
        playersInteractor.fetchEvent("testEventId").test()
                .assertValue(PartialPlayersViewState.EventFetched(testEvent))
    }
}