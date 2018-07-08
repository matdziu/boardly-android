package com.boardly.addevent

import com.boardly.retrofit.gamesearch.models.Game
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class AddEventViewModelTest {

    private val addEventInteractor: AddEventInteractor = mock()
    private val addEventViewModel = AddEventViewModel(addEventInteractor)
    private val addEventViewRobot = AddEventViewRobot(addEventViewModel)

    @Test
    fun testGamePickEvent() {
        val fetchedGame = Game(1, "Galaxy", "2008")
        whenever(addEventInteractor.fetchGameDetails(any()))
                .thenReturn(Observable.just(PartialAddEventViewState.GameDetailsFetched(fetchedGame)))

        addEventViewRobot.pickGame("1")

        addEventViewRobot.assertViewStates(
                AddEventViewState(),
                AddEventViewState(selectedGameValid = true),
                AddEventViewState(selectedGame = fetchedGame))
    }

    @Test
    fun testPlacePickEvent() {
        addEventViewRobot.pickPlace()

        addEventViewRobot.assertViewStates(
                AddEventViewState(),
                AddEventViewState(selectedPlaceValid = true))
    }

    @Test
    fun whenEventNameIsBlankShowEventNameError() {
        val inputData = InputData(
                eventName = "  ",
                gameId = "1",
                maxPlayers = 1,
                placeId = "testPlaceId")

        addEventViewRobot.emitInputData(inputData)

        addEventViewRobot.assertViewStates(
                AddEventViewState(),
                AddEventViewState(eventNameValid = false))
    }

    @Test
    fun whenNumberOfPlayersIsZeroShowNumberOfPlayersError() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "1",
                maxPlayers = 0,
                placeId = "testPlaceId")

        addEventViewRobot.emitInputData(inputData)

        addEventViewRobot.assertViewStates(
                AddEventViewState(),
                AddEventViewState(numberOfPlayersValid = false))
    }

    @Test
    fun whenNoGameIsSelectedShowSelectedGameError() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "",
                maxPlayers = 1,
                placeId = "testPlaceId")

        addEventViewRobot.emitInputData(inputData)

        addEventViewRobot.assertViewStates(
                AddEventViewState(),
                AddEventViewState(selectedGameValid = false))
    }

    @Test
    fun whenNoPlaceIsSelectedShowSelectedPlaceError() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "1",
                maxPlayers = 1)

        addEventViewRobot.emitInputData(inputData)

        addEventViewRobot.assertViewStates(
                AddEventViewState(),
                AddEventViewState(selectedPlaceValid = false))
    }

    @Test
    fun whenAllFieldsAreValidShowSuccess() {
        val inputData = InputData(
                eventName = "Let's go",
                gameId = "1",
                maxPlayers = 1,
                placeId = "testPlaceId")
        whenever(addEventInteractor.addEvent(any())).thenReturn(Observable.just(PartialAddEventViewState.SuccessState()))

        addEventViewRobot.emitInputData(inputData)

        addEventViewRobot.assertViewStates(
                AddEventViewState(),
                AddEventViewState(progress = true),
                AddEventViewState(success = true))
    }
}