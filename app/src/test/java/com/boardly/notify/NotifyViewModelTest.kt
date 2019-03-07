package com.boardly.notify

import com.boardly.notify.models.NotifySettings
import com.boardly.retrofit.gameservice.models.Game
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import org.junit.Test

class NotifyViewModelTest {

    private val testNotifySettings = NotifySettings(radius = 100.0, gameId = "1", gameName = "Monopoly")
    private val testGame = Game(1, "Monopoly", "1994", "url/thumbnail", "url/picture")
    private val notifyInteractor: NotifyInteractor = mock {
        on { it.fetchNotifySettings() } doReturn Observable.just(PartialNotifyViewState.NotifySettingsFetched(testNotifySettings))
                .cast(PartialNotifyViewState::class.java)
        on { it.deleteNotifications() } doReturn Observable.just(PartialNotifyViewState.SuccessState())
                .cast(PartialNotifyViewState::class.java)
        on { it.updateNotifySettings(any()) } doReturn Observable.just(PartialNotifyViewState.SuccessState())
                .cast(PartialNotifyViewState::class.java)
        on { it.fetchGameDetails(any()) } doReturn Observable.just(PartialNotifyViewState.GameDetailsFetched(testGame))
                .cast(PartialNotifyViewState::class.java)
    }
    private val notifyViewModel = NotifyViewModel(notifyInteractor)
    private val notifyViewRobot = NotifyViewRobot(notifyViewModel)

    @Test
    fun testSuccessfulNotifySettingsFetching() {
        notifyViewRobot.triggerNotifySettingsFetch(false)
        notifyViewRobot.triggerNotifySettingsFetch(true)
        notifyViewRobot.assertViewStates(
                NotifyViewState(),
                NotifyViewState(progress = true),
                NotifyViewState(notifySettings = testNotifySettings))
    }

    @Test
    fun testSuccessfulNotifySettingsDeletion() {
        notifyViewRobot.deleteNotifySettings()
        notifyViewRobot.assertViewStates(
                NotifyViewState(),
                NotifyViewState(progress = true),
                NotifyViewState(success = true))
    }

    @Test
    fun testPlacePickEvent() {
        notifyViewRobot.emitNotifySettings(NotifySettings())
        notifyViewRobot.emitPlacePickEvent()
        notifyViewRobot.assertViewStates(
                NotifyViewState(),
                NotifyViewState(selectedPlaceValid = false),
                NotifyViewState(selectedPlaceValid = true))
    }

    @Test
    fun givenUserUpdatesNotifySettingsWhenPlaceIsNotPickedThenShowError() {
        notifyViewRobot.emitNotifySettings(NotifySettings())
        notifyViewRobot.assertViewStates(
                NotifyViewState(),
                NotifyViewState(selectedPlaceValid = false))
    }

    @Test
    fun givenUserUpdatesNotifySettingsWhenPlaceIsPickedThenShowSuccess() {
        notifyViewRobot.emitNotifySettings(NotifySettings(locationName = "Cracow"))
        notifyViewRobot.assertViewStates(
                NotifyViewState(),
                NotifyViewState(progress = true),
                NotifyViewState(success = true))
    }
}