package com.boardly.pickgame

import com.boardly.retrofit.gameservice.models.SearchResult
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class PickGameViewModelTest {

    private val searchResults = listOf(SearchResult(1, "Galaxy Truck", "2007"))
    private val pickGameInteractor: PickGameInteractor = mock {
        on { it.fetchSearchResults(any()) } doReturn
                Observable.just(PartialPickGameViewState.ResultsFetchedState(searchResults))
                        .cast(PartialPickGameViewState::class.java)
    }
    private val pickGameViewModel = PickGameViewModel(pickGameInteractor)
    private val pickGameViewRobot = PickGameViewRobot(pickGameViewModel)

    @Test
    fun testWhenQueryIsJustOneLetter() {
        pickGameViewRobot.emitQuery("a")

        pickGameViewRobot.assertViewStates(
                PickGameViewState())
    }

    @Test
    fun testWhenSearchEndsIsSuccess() {
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
        whenever(pickGameInteractor.fetchSearchResults(any()))
                .thenReturn(Observable.just(PartialPickGameViewState.ErrorState(error, unacceptedQuery)))

        pickGameViewRobot.emitQuery(unacceptedQuery)

        pickGameViewRobot.assertViewStates(
                PickGameViewState(),
                PickGameViewState(progress = true),
                PickGameViewState(error = error, unacceptedQuery = unacceptedQuery))
    }
}