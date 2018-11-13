package com.boardly.notify

import com.boardly.notify.models.NotifySettings
import com.boardly.notify.network.NotifyService
import com.boardly.retrofit.gameservice.GameService
import com.boardly.retrofit.gameservice.models.DetailsResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class NotifyInteractor @Inject constructor(private val gameService: GameService,
                                           private val notifyService: NotifyService) {

    fun fetchGameDetails(gameId: String): Observable<PartialNotifyViewState> {
        return gameService.gameDetails(gameId)
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn { DetailsResponse() }
                .map { PartialNotifyViewState.GameDetailsFetched(it.game) }
    }

    fun updateNotifySettings(notifySettings: NotifySettings): Observable<PartialNotifyViewState> {
        return notifyService.updateNotifySettings(notifySettings)
                .map { PartialNotifyViewState.SuccessState() }
    }

    fun fetchNotifySettings(): Observable<PartialNotifyViewState> {
        return notifyService.fetchNotifySettings()
                .map { PartialNotifyViewState.NotifySettingsFetched(it) }
    }

    fun deleteNotifications(): Observable<PartialNotifyViewState> {
        return notifyService.deleteNotifications()
                .map { PartialNotifyViewState.SuccessState() }
    }
}