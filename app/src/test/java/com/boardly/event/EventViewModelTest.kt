package com.boardly.event

import com.boardly.retrofit.gamesearch.models.Game
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class EventViewModelTest {

    private val eventInteractor: EventInteractor = mock()
    private val eventViewModel = EventViewModel(eventInteractor)
    private val eventViewRobot = EventViewRobot(eventViewModel)

    @Test
    fun testGamePickEvent() {
        val fetchedGame = Game(1, "Galaxy", "2008")
        whenever(eventInteractor.fetchGameDetails(any()))
                .thenReturn(Observable.just(PartialEventViewState.GameDetailsFetched(fetchedGame)))

        eventViewRobot.pickGame("1")

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(selectedGameValid = true),
                EventViewState(selectedGame = fetchedGame))
    }

    @Test
    fun testPlacePickEvent() {
        eventViewRobot.pickPlace()

        eventViewRobot.assertViewStates(
                EventViewState(),
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