package com.boardly.gamescollection

import com.boardly.gamescollection.models.CollectionGame
import com.boardly.gamescollection.network.GamesCollectionService
import com.boardly.retrofit.gameservice.GameService
import com.boardly.retrofit.gameservice.models.DetailsResponse
import com.boardly.retrofit.gameservice.models.Game
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class GamesCollectionInteractorTest {

    private val gameService: GameService = mock()
    private val gamesCollectionService: GamesCollectionService = mock()
    private val gamesCollectionInteractor = GamesCollectionInteractor(gameService, gamesCollectionService)

    @Test
    fun testSuccessfulGamesFetching() {
        val testGames = listOf(CollectionGame(id = "1", name = "Inis"), CollectionGame(id = "2", name = "Monopoly"))
        whenever(gamesCollectionService.fetchGames(any())).thenReturn(Observable.just(testGames))
        gamesCollectionInteractor.fetchGames("testCollectId").test()
                .assertValue(PartialGamesCollectionViewState.CollectionFetched(testGames))
    }

    @Test
    fun testSuccessfulGameAdding() {
        val testDetailsResponse = DetailsResponse(Game(image = "path/to/image"))
        whenever(gamesCollectionService.addGame("collectionId", CollectionGame(name = "1"), 1)).thenReturn(Observable.just(true))
        whenever(gameService.gameDetails(any())).thenReturn(Observable.just(testDetailsResponse))
        gamesCollectionInteractor.addGame("collectionId", CollectionGame(name = "1"), 1)
                .test()
                .assertValues(
                        PartialGamesCollectionViewState.SuccessState(),
                        PartialGamesCollectionViewState.SuccessState(false))
    }

    @Test
    fun testSuccessfulGameDeletion() {
        whenever(gamesCollectionService.deleteGame(any(), any())).thenReturn(Observable.just(true))
        gamesCollectionInteractor.deleteGame("collectionId", "gameId").test()
                .assertValues(
                        PartialGamesCollectionViewState.SuccessState(),
                        PartialGamesCollectionViewState.SuccessState(false))
    }
}