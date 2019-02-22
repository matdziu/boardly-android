package com.boardly.gamescollection

import com.boardly.gamescollection.models.CollectionGame
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class GamesCollectionViewModelTest {
    private val testGames =
            listOf(CollectionGame(id = "1", name = "Inis"),
                    CollectionGame(id = "2", name = "Monopoly Lodon"),
                    CollectionGame(id = "3", name = "Monopoly Krakow"),
                    CollectionGame(id = "4", name = "Eurobusiness"))
    private val gamesCollectionInteractor: GamesCollectionInteractor = mock {
        on { it.fetchGames(any()) } doReturn Observable.just(PartialGamesCollectionViewState.CollectionFetched(testGames))
                .cast(PartialGamesCollectionViewState::class.java)
        on { it.addGame(any(), any()) } doReturn Observable.just(PartialGamesCollectionViewState.SuccessState())
                .cast(PartialGamesCollectionViewState::class.java)
        on { it.deleteGame(any(), any()) } doReturn Observable.just(PartialGamesCollectionViewState.SuccessState())
                .cast(PartialGamesCollectionViewState::class.java)
    }
    private val gamesCollectionViewModel = GamesCollectionViewModel(gamesCollectionInteractor)
    private val gamesCollectionViewRobot = GamesCollectionViewRobot(gamesCollectionViewModel)

    @Test
    fun testInitialCollectionFetchWithProgress() {
        gamesCollectionViewRobot.fetchInitialData(true)
        gamesCollectionViewRobot.assertViewStates(
                GamesCollectionViewState(),
                GamesCollectionViewState(progress = true),
                GamesCollectionViewState(progress = false, games = testGames))
    }

    @Test
    fun testInitialCollectionFetchWithoutProgress() {
        gamesCollectionViewRobot.fetchInitialData(false)
        gamesCollectionViewRobot.assertViewStates(
                GamesCollectionViewState(),
                GamesCollectionViewState(progress = false, games = testGames))
    }

    @Test
    fun testSuccessfulGameAdding() {
        gamesCollectionViewRobot.addGame(CollectionGame(id = "1", name = "Inis"))
        gamesCollectionViewRobot.assertViewStates(
                GamesCollectionViewState(),
                GamesCollectionViewState(success = true))
    }

    @Test
    fun testSuccessfulGameDeletion() {
        gamesCollectionViewRobot.deleteGame("111")
        gamesCollectionViewRobot.assertViewStates(
                GamesCollectionViewState(),
                GamesCollectionViewState(success = true))
    }

    @Test
    fun testQueryLogic() {
        val testGamesAfterFirstQuery =
                listOf(CollectionGame(id = "2", name = "Monopoly Lodon"),
                        CollectionGame(id = "3", name = "Monopoly Krakow"))
        val testGamesAfterSecondQuery =
                listOf(CollectionGame(id = "1", name = "Inis"),
                        CollectionGame(id = "4", name = "Eurobusiness"))
        gamesCollectionViewRobot.fetchInitialData(true)
        gamesCollectionViewRobot.emitQuery("  MONOPOly   ")
        gamesCollectionViewRobot.emitQuery("in")
        gamesCollectionViewRobot.emitQuery("addajoasjdio")
        gamesCollectionViewRobot.assertViewStates(
                GamesCollectionViewState(),
                GamesCollectionViewState(progress = true),
                GamesCollectionViewState(progress = false, games = testGames),
                GamesCollectionViewState(progress = false, games = testGamesAfterFirstQuery),
                GamesCollectionViewState(progress = false, games = testGamesAfterSecondQuery),
                GamesCollectionViewState(progress = false, games = listOf()))
    }
}