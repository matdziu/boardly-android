package com.boardly.event

import com.boardly.event.models.GamePickEvent
import com.boardly.event.models.GamePickType
import com.boardly.event.network.EventService
import com.boardly.retrofit.gamesearch.GameSearchService
import com.boardly.retrofit.gamesearch.models.DetailsResponse
import com.boardly.retrofit.gamesearch.models.Game
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class EventInteractorTest {

    @Test
    fun testSuccessfulFirstGameDetailsFetching() {
        val testGame = Game(1, "Monopoly")
        val detailsResponse = DetailsResponse(testGame)
        val gameSearchService: GameSearchService = mock {
            on { it.details(any()) } doReturn Observable.just(detailsResponse)
        }
        val eventService: EventService = mock()
        val eventInteractor = EventInteractor(gameSearchService, eventService)

        eventInteractor.fetchGameDetails(GamePickEvent("testGameId")).test()
                .assertValue(PartialEventViewState.GameDetailsFetched(testGame, GamePickType.FIRST))
    }

    @Test
    fun testSuccessfulSecondGameDetailsFetching() {
        val testGame = Game(1, "Monopoly")
        val detailsResponse = DetailsResponse(testGame)
        val gameSearchService: GameSearchService = mock {
            on { it.details(any()) } doReturn Observable.just(detailsResponse)
        }
        val eventService: EventService = mock()
        val eventInteractor = EventInteractor(gameSearchService, eventService)

        eventInteractor.fetchGameDetails(GamePickEvent("testGameId", GamePickType.SECOND)).test()
                .assertValue(PartialEventViewState.GameDetailsFetched(testGame, GamePickType.SECOND))
    }

    @Test
    fun testSuccessfulThirdGameDetailsFetching() {
        val testGame = Game(1, "Monopoly")
        val detailsResponse = DetailsResponse(testGame)
        val gameSearchService: GameSearchService = mock {
            on { it.details(any()) } doReturn Observable.just(detailsResponse)
        }
        val eventService: EventService = mock()
        val eventInteractor = EventInteractor(gameSearchService, eventService)

        eventInteractor.fetchGameDetails(GamePickEvent("testGameId", GamePickType.THIRD)).test()
                .assertValue(PartialEventViewState.GameDetailsFetched(testGame, GamePickType.THIRD))
    }

    @Test
    fun testGameDetailsFetchingWithError() {
        val exception = Exception("")
        val gameSearchService: GameSearchService = mock {
            on { it.details(any()) } doReturn Observable.error(exception)
        }
        val eventService: EventService = mock()
        val eventInteractor = EventInteractor(gameSearchService, eventService)

        eventInteractor.fetchGameDetails(GamePickEvent("testGameId")).test()
                .assertValue(PartialEventViewState.GameDetailsFetched(Game(), GamePickType.FIRST))
    }

    @Test
    fun testSuccessfulEventAddition() {
        val gameSearchService: GameSearchService = mock()
        val eventService: EventService = mock {
            on { it.addEvent(any()) } doReturn Observable.just(true)
        }
        val eventInteractor = EventInteractor(gameSearchService, eventService)

        eventInteractor.addEvent(InputData()).test()
                .assertValue { it is PartialEventViewState.SuccessState }
    }

    @Test
    fun testSuccessfulEventEditing() {
        val gameSearchService: GameSearchService = mock()
        val eventService: EventService = mock {
            on { it.editEvent(any()) } doReturn Observable.just(true)
        }
        val eventInteractor = EventInteractor(gameSearchService, eventService)

        eventInteractor.editEvent(InputData()).test()
                .assertValue { it is PartialEventViewState.SuccessState }
    }

    @Test
    fun testSuccessfulEventDeletion() {
        val gameSearchService: GameSearchService = mock()
        val eventService: EventService = mock {
            on { it.deleteEvent(any()) } doReturn Observable.just(true)
        }
        val eventInteractor = EventInteractor(gameSearchService, eventService)

        eventInteractor.deleteEvent("testEventId").test()
                .assertValue { it is PartialEventViewState.RemovedState }
    }
}