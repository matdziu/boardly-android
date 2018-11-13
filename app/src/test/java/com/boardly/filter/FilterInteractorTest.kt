package com.boardly.filter

import com.boardly.retrofit.gamesearch.GameSearchService
import com.boardly.retrofit.gamesearch.models.DetailsResponse
import com.boardly.retrofit.gamesearch.models.Game
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
        val gameSearchService: GameSearchService = mock {
            on { it.boardGameDetails(any()) } doReturn
                    Observable.just(DetailsResponse(testGameDetail))
        }
        val filterInteractor = FilterInteractor(gameSearchService)

        filterInteractor.fetchGameDetails("testGameId").test()
                .assertValue(PartialFilterViewState.GameDetailsFetched(testGameDetail))
    }

    @Test
    fun testGameDetailsFetchingWithError() {
        val gameSearchService: GameSearchService = mock {
            on { it.boardGameDetails(any()) } doReturn Observable.error(Exception(""))
        }
        val filterInteractor = FilterInteractor(gameSearchService)

        filterInteractor.fetchGameDetails("testGameId").test()
                .assertValue(PartialFilterViewState.GameDetailsFetched(Game()))
    }
}