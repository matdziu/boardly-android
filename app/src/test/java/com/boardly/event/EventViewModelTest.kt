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
    fun whenEventNameIsBlankShowEventNameError() {
        val inputData = InputData(
                eventName = "  ",
                gameId = "1",
                placeName = "Domowka")

        eventViewRobot.emitInputData(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(eventNameValid = false))
    }

    @Test
    fun whenNoGameIsSelectedShowSelectedGameError() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "",
                placeName = "Domowka")

        eventViewRobot.emitInputData(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(selectedGameValid = false))
    }

    @Test
    fun whenNoPlaceIsSelectedShowSelectedPlaceError() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "1")

        eventViewRobot.emitInputData(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(selectedPlaceValid = false))
    }

    @Test
    fun whenAllFieldsAreValidShowSuccess() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "1",
                placeName = "Domowka")
        whenever(eventInteractor.addEvent(any())).thenReturn(Observable.just(PartialEventViewState.SuccessState()))

        eventViewRobot.emitInputData(inputData)

        eventViewRobot.assertViewStates(
                EventViewState(),
                EventViewState(progress = true),
                EventViewState(success = true))
    }
}