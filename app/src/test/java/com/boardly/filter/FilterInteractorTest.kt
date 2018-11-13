package com.boardly.filter

import com.boardly.retrofit.gameservice.GameService
import com.boardly.retrofit.gameservice.models.DetailsResponse
import com.boardly.retrofit.gameservice.models.Game
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class FilterInteractorTest {

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testSuccessfulGameDetailsFetching() {
        val testGameDetail = Game(1, "Monopoly", "1999")
        val gameService: GameService = mock {
            on { it.gameDetails(any()) } doReturn
                    Observable.just(DetailsResponse(testGameDetail))
        }
        val filterInteractor = FilterInteractor(gameService)

        filterInteractor.fetchGameDetails("testGameId").test()
                .assertValue(PartialFilterViewState.GameDetailsFetched(testGameDetail))
    }

    @Test
    fun testGameDetailsFetchingWithError() {
        val gameService: GameService = mock {
            on { it.gameDetails(any()) } doReturn Observable.error(Exception(""))
        }
        val filterInteractor = FilterInteractor(gameService)

        filterInteractor.fetchGameDetails("testGameId").test()
                .assertValue(PartialFilterViewState.GameDetailsFetched(Game()))
    }
}