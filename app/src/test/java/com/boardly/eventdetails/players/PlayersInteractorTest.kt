package com.boardly.eventdetails.players

import com.boardly.base.rating.models.RateInput
import com.boardly.common.players.models.Player
import com.boardly.eventdetails.players.network.PlayersService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class PlayersInteractorTest {

    private val testAcceptedPlayersList = listOf(Player(
            id = "acceptedTestId",
            rating = 5.0,
            ratedOrSelf = true))
    private val playersService: PlayersService = mock {
        on { it.getAcceptedPlayers(any()) } doReturn Observable.just(testAcceptedPlayersList)
        on { it.sendRating(any()) } doReturn Observable.just(true)
    }
    private val playersInteractor = PlayersInteractor(playersService)

    @Test
    fun testSuccessfulAcceptedPlayersFetching() {
        playersInteractor.fetchAcceptedPlayers("testEventId").test()
                .assertValue(PartialPlayersViewState.AcceptedListState(testAcceptedPlayersList))
    }

    @Test
    fun testSuccessfulRatingSending() {
        playersInteractor.sendRating(RateInput(5, "testPlayerId", "testEventId")).test()
                .assertValue { it is PartialPlayersViewState.RatingSent }
    }
}