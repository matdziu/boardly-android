package com.boardly.notify

import com.boardly.notify.models.NotifySettings
import com.boardly.notify.network.NotifyService
import com.boardly.retrofit.gamesearch.GameSearchService
import com.boardly.retrofit.gamesearch.models.DetailsResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class NotifyInteractor @Inject constructor(private val gameSearchService: GameSearchService,
                                           private val notifyService: NotifyService) {

    fun fetchGameDetails(gameId: String): Observable<PartialNotifyViewState> {
        return gameSearchService.boardGameDetails(gameId)
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