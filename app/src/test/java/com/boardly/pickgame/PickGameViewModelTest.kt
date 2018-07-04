package com.boardly.pickgame

import com.boardly.retrofit.gamesearch.models.SearchResult
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class PickGameViewModelTest {

    private val pickGameInteractor: PickGameInteractor = mock()

    @Test
    fun testWhenQueryIsJustOneLetter() {
        val searchResults = listOf(SearchResult(1, "Galaxy Truck", "2007"))
        whenever(pickGameInteractor.fetchSearchResults(any())).thenReturn(Observable.just(PartialPickGameViewState.ResultsFetchedState(searchResults)))
        val pickGameViewModel = PickGameViewModel(pickGameInteractor)
        val pickGameViewRobot = PickGameViewRobot(pickGameViewModel)

        pickGameViewRobot.emitQuery("a")

        pickGameViewRobot.assertViewStates(
                PickGameViewState())
    }

    @Test
    fun testWhenSearchEndsIsSuccess() {
        val searchResults = listOf(SearchResult(1, "Galaxy Truck", "2007"))
        whenever(pickGameInteractor.fetchSearchResults(any())).thenReturn(Observable.just(PartialPickGameViewState.ResultsFetchedState(searchResults)))
        val pickGameViewModel = PickGameViewModel(pickGameInteractor)
        val pickGameViewRobot = PickGameViewRobot(pickGameViewModel)

        pickGameViewRobot.emitQuery("test test")

        pickGameViewRobot.assertViewStates(
                PickGameViewState(),
                PickGameViewState(progress = true),
                PickGameViewState(searchResults = searchResults))
    }

    @Test
    fun testWhenSearchEndsInError() {
        val error = Throwable("error")
        val unacceptedQuery = "ma"
        whenever(pickGameInteractor.fetchSearchResults(any())).thenReturn(Observable.just(PartialPickGameViewState.ErrorState(error, unacceptedQuery)))
        val pickGameViewModel = PickGameViewModel(pickGameInteractor)
        val pickGameViewRobot = PickGameViewRobot(pickGameViewModel)

        pickGameViewRobot.emitQuery(unacceptedQuery)

        pickGameViewRobot.assertViewStates(
                PickGameViewState(),
                PickGameViewState(progress = true),
                PickGameViewState(error = error, unacceptedQuery = unacceptedQuery))
    }
}