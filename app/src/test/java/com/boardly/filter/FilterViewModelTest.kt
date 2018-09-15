package com.boardly.filter

import com.boardly.retrofit.gamesearch.models.Game
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class FilterViewModelTest {

    private val testGame = Game(image = "url/to/image")
    private val filterInteractor: FilterInteractor = mock {
        on { it.fetchGameDetails(any()) } doReturn
                Observable.just(PartialFilterViewState.GameDetailsFetched(testGame))
                        .cast(PartialFilterViewState::class.java)
    }
    private val filterViewModel = FilterViewModel(filterInteractor)
    private val filterViewRobot = FilterViewRobot(filterViewModel)

    @Test
    fun testSuccessfulGameDetailsFetching() {
        filterViewRobot.emitGameId("testGameId")

        filterViewRobot.assertViewStates(
                FilterViewState(),
                FilterViewState(gameImageUrl = testGame.image))
    }

    @Test
    fun whenLocationIsProcessedViewStateIndicatesIt() {
        filterViewRobot.emitLocationProcessing(true)
        filterViewRobot.emitLocationProcessing(false)

        filterViewRobot.assertViewStates(
                FilterViewState(),
                FilterViewState(locationProcessing = true),
                FilterViewState(locationProcessing = false))
    }
}