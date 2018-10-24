package com.boardly.event

import com.boardly.event.models.GamePickEvent
import com.boardly.event.models.GamePickType
import com.boardly.retrofit.gamesearch.models.Game
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class EventViewModelTest {

    private val eventInteractor: EventInteractor = mock()
    private val eventViewModel = EventViewModel(eventInteractor, mock())
    private val eventViewRobot = EventViewRobot(eventViewModel)

    @Test
    fun testFirstGamePickEvent() {
        val fetchedGame = Game(1, "Galaxy", "2008")
        whenever(eventInteractor.fetchGameDetails(any()))
                .thenReturn(Observable.just(PartialEventViewState.GameDetailsFetched(fetchedGame, GamePickType.FIRST)))

        eventViewRobot.pickGame(GamePickEvent("1"))

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(selectedGameValid = true),
                EventViewState(selectedGame = fetchedGame))
    }

    @Test
    fun testSecondGamePickEvent() {
        val fetchedGame = Game(1, "Galaxy", "2008")
        whenever(eventInteractor.fetchGameDetails(any()))
                .thenReturn(Observable.just(PartialEventViewState.GameDetailsFetched(fetchedGame, GamePickType.SECOND)))

        eventViewRobot.pickGame(GamePickEvent("1", GamePickType.SECOND))

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(selectedGameValid = true),
                EventViewState(selectedGame2 = fetchedGame))
    }

    @Test
    fun testThirdGamePickEvent() {
        val fetchedGame = Game(1, "Galaxy", "2008")
        whenever(eventInteractor.fetchGameDetails(any()))
                .thenReturn(Observable.just(PartialEventViewState.GameDetailsFetched(fetchedGame, GamePickType.THIRD)))

        eventViewRobot.pickGame(GamePickEvent("1", GamePickType.THIRD))

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(selectedGameValid = true),
                EventViewState(selectedGame3 = fetchedGame))
    }

    @Test
    fun testPlacePickEvent() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "1")

        eventViewRobot.addEvent(inputData)
        eventViewRobot.pickPlace()

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(selectedPlaceValid = false),
                EventViewState(selectedPlaceValid = true))
    }

    @Test
    fun givenUserAddsEventWhenEventNameIsBlankThenShowEventNameError() {
        val inputData = InputData(
                eventName = "  ",
                gameId = "1",
                placeName = "Domowka")

        eventViewRobot.addEvent(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(eventNameValid = false))
    }

    @Test
    fun givenUserAddsEventWhenNoGameIsSelectedThenShowSelectedGameError() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "",
                placeName = "Domowka")

        eventViewRobot.addEvent(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(selectedGameValid = false))
    }

    @Test
    fun givenUserAddsEventWhenNoPlaceIsSelectedThenShowSelectedPlaceError() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "1")

        eventViewRobot.addEvent(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(selectedPlaceValid = false))
    }

    @Test
    fun givenUserAddsEventWhenAllFieldsAreValidThenShowSuccess() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "1",
                placeName = "Domowka")
        whenever(eventInteractor.addEvent(any())).thenReturn(Observable.just(PartialEventViewState.SuccessState()))

        eventViewRobot.addEvent(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(progress = true),
                EventViewState(success = true))
    }


    @Test
    fun givenUserEditsEventWhenEventNameIsBlankThenShowEventNameError() {
        val inputData = InputData(
                eventName = "  ",
                gameId = "1",
                placeName = "Domowka")

        eventViewRobot.editEvent(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(eventNameValid = false))
    }

    @Test
    fun givenUserEditsEventWhenNoGameIsSelectedThenShowSelectedGameError() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "",
                placeName = "Domowka")

        eventViewRobot.editEvent(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(selectedGameValid = false))
    }

    @Test
    fun givenUserEditsEventWhenNoPlaceIsSelectedThenShowSelectedPlaceError() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "1")

        eventViewRobot.editEvent(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(selectedPlaceValid = false))
    }

    @Test
    fun givenUserEditsEventWhenAllFieldsAreValidThenShowSuccess() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "1",
                placeName = "Domowka")
        whenever(eventInteractor.editEvent(any())).thenReturn(Observable.just(PartialEventViewState.SuccessState()))

        eventViewRobot.editEvent(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(progress = true),
                EventViewState(success = true))
    }

    @Test
    fun givenUserEditsEventWhenDeletingEventThenShowRemovedViewState() {
        whenever(eventInteractor.deleteEvent(any())).thenReturn(Observable.just(PartialEventViewState.RemovedState()))

        eventViewRobot.deleteEvent("testEventId")

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(removed = true))
    }
}