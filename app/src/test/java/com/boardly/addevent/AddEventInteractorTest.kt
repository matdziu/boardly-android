package com.boardly.addevent

import com.boardly.addevent.network.AddEventService
import com.boardly.retrofit.gamesearch.GameSearchService
import com.boardly.retrofit.gamesearch.models.DetailsResponse
import com.boardly.retrofit.gamesearch.models.Game
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class AddEventInteractorTest {

    @Test
    fun testSuccessfulGameDetailsFetching() {
        val testGame = Game(1, "Monopoly")
        val detailsResponse = DetailsResponse(testGame)
        val gameSearchService: GameSearchService = mock {
            on { it.details(any()) } doReturn Observable.just(detailsResponse)
        }
        val addEventService: AddEventService = mock()
        val addEventInteractor = AddEventInteractor(gameSearchService, addEventService)

        addEventInteractor.fetchGameDetails("testGameId").test()
                .assertValue(PartialAddEventViewState.GameDetailsFetched(testGame))
    }

    @Test
    fun testGameDetailsFetchingWithError() {
        val exception = Exception("")
        val gameSearchService: GameSearchService = mock {
            on { it.details(any()) } doReturn Observable.error(exception)
        }
        val addEventService: AddEventService = mock()
        val addEventInteractor = AddEventInteractor(gameSearchService, addEventService)

        addEventInteractor.fetchGameDetails("testGameId").test()
                .assertValue(PartialAddEventViewState.GameDetailsFetched(Game()))
    }

    @Test
    fun testSuccessfulEventAddition() {
        val gameSearchService: GameSearchService = mock()
        val addEventService: AddEventService = mock {
            on { it.addEvent(any()) } doReturn Observable.just(true)
        }
        val addEventInteractor = AddEventInteractor(gameSearchService, addEventService)

        addEventInteractor.addEvent(InputData()).test()
                .assertValue { it is PartialAddEventViewState.SuccessState }
    }
}