package com.boardly.manageplace

import com.boardly.discover.models.Place
import com.boardly.manageplace.models.PlaceInputData
import com.boardly.manageplace.network.ManagePlaceService
import io.reactivex.Observable
import javax.inject.Inject

class ManagePlaceInteractor @Inject constructor(private val managePlaceService: ManagePlaceService) {

    fun fetchPlaceData(): Observable<PartialManagePlaceViewState> {
        return managePlaceService.fetchManagedPlaceId()
                .flatMap { managedPlaceId ->
                    if (managedPlaceId.isEmpty()) {
                        Observable.just(PartialManagePlaceViewState.PartnershipCheckState(false))
                    } else {
                        managePlaceService
                                .fetchPlaceData(managedPlaceId)
                                .flatMap { emitPlaceDataFetchedState(it) }
                                .cast(PartialManagePlaceViewState::class.java)
                                .startWith(PartialManagePlaceViewState.PartnershipCheckState(true))
                    }
                }
    }

    private fun emitPlaceDataFetchedState(place: Place)
            : Observable<PartialManagePlaceViewState.PlaceDataFetched> {
        val placeDataFetchedState = PartialManagePlaceViewState.PlaceDataFetched(place)
        return Observable.just(placeDataFetchedState).startWith(placeDataFetchedState.copy(render = true))
    }

    fun savePlaceData(placeInputData: PlaceInputData): Observable<PartialManagePlaceViewState> {
        return managePlaceService.fetchManagedPlaceId()
                .flatMap {
                    managePlaceService.savePlaceData(it, placeInputData)
                            .map { PartialManagePlaceViewState.SuccessfulUpdateState }
                }
    }
}