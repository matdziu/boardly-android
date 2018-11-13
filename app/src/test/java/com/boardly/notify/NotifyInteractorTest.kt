package com.boardly.notify

import com.boardly.notify.models.NotifySettings
import com.boardly.notify.network.NotifyService
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

class NotifyInteractorTest {

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    private val testGame = Game(1, "Monopoly")
    private val detailsResponse = DetailsResponse(testGame)
    private val testNotifySettings = NotifySettings(radius = 100.0, gameId = "1", gameName = "Monopoly")
    private val gameSearchService: GameSearchService = mock {
        on { it.boardGameDetails("testGameId") } doReturn Observable.just(detailsResponse)
    }
    private val notifyService: NotifyService = mock {
        on { it.deleteNotifications() } doReturn Observable.just(true)
        on { it.fetchNotifySettings() } doReturn Observable.just(testNotifySettings)
        on { it.updateNotifySettings(any()) } doReturn Observable.just(true)
    }
    private val notifyInteractor = NotifyInteractor(gameSearchService, notifyService)

    @Test
    fun testSuccessfulGameDetailsFetching() {
        notifyInteractor.fetchGameDetails("testGameId")
                .test()
                .assertValues(PartialNotifyViewState.GameDetailsFetched(testGame))
    }

    @Test
    fun testSuccessfulNotifySettingsUpdate() {
        notifyInteractor.updateNotifySettings(NotifySettings())
                .test()
                .assertValue { it is PartialNotifyViewState.SuccessState }
    }

    @Test
    fun testSuccessfulNotifySettingsFetch() {
        notifyInteractor.fetchNotifySettings()
                .test()
                .assertValue(PartialNotifyViewState.NotifySettingsFetched(testNotifySettings))
    }

    @Test
    fun testSuccessfulNotificationsDeletion() {
        notifyInteractor.deleteNotifications()
                .test()
                .assertValue { it is PartialNotifyViewState.SuccessState }
    }
}