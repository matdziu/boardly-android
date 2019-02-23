package com.boardly.manageplace

import com.boardly.discover.models.Place
import com.boardly.manageplace.network.ManagePlaceService
import io.reactivex.Observable
import javax.inject.Inject

class ManagePlaceInteractor @Inject constructor(private val managePlaceService: ManagePlaceService) {

    fun fetchPlaceData(): Observable<PartialManagePlaceViewState> {
        return managePlaceService.fetchManagedPlaceId()
                .flatMap { managedPlaceId ->
                    if (managedPlaceId.isEmpty()) Observable.just(PartialManagePlaceViewState.PartnershipCheckState(false))
                    else managePlaceService.fetchPlaceData(managedPlaceId).flatMap { emitPlaceDataFetchedState(it) }
                }
    }

    private fun emitPlaceDataFetchedState(place: Place)
            : Observable<PartialManagePlaceViewState.PlaceDataFetched> {
        val placeDataFetchedState = PartialManagePlaceViewState.PlaceDataFetched(place)
        return Observable.just(placeDataFetchedState).startWith(placeDataFetchedState.copy(render = true))
    }

    fun savePlaceData(place: Place): Observable<PartialManagePlaceViewState> {
        return managePlaceService.fetchManagedPlaceId()
                .flatMap {
                    managePlaceService.savePlaceData(it, place)
                            .map { PartialManagePlaceViewState.SuccessfulUpdateState }
                }
    }
}