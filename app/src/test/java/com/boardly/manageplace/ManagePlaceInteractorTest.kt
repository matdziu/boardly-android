package com.boardly.manageplace

import com.boardly.discover.models.Place
import com.boardly.manageplace.network.ManagePlaceService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class ManagePlaceInteractorTest {

    private val managePlaceService: ManagePlaceService = mock()
    private val managePlaceInteractor = ManagePlaceInteractor(managePlaceService)

    @Test
    fun testFetchingPlaceDataForPartner() {
        whenever(managePlaceService.fetchManagedPlaceId()).thenReturn(Observable.just("someId"))
        whenever(managePlaceService.fetchPlaceData(any())).thenReturn(Observable.just(Place(id = "1")))
        managePlaceInteractor.fetchPlaceData().test()
                .assertValues(
                        PartialManagePlaceViewState.PartnershipCheckState(true),
                        PartialManagePlaceViewState.PlaceDataFetched(Place(id = "1"), render = true),
                        PartialManagePlaceViewState.PlaceDataFetched(Place(id = "1")))
    }

    @Test
    fun testFetchingPlaceDataForNonPartner() {
        whenever(managePlaceService.fetchManagedPlaceId()).thenReturn(Observable.just(""))
        whenever(managePlaceService.fetchPlaceData(any())).thenReturn(Observable.just(Place(id = "1")))
        managePlaceInteractor.fetchPlaceData().test()
                .assertValues(
                        PartialManagePlaceViewState.PartnershipCheckState(false))
    }

    @Test
    fun testSuccessfulPlaceSaving() {
        whenever(managePlaceService.savePlaceData(any(), any())).thenReturn(Observable.just(true))
        whenever(managePlaceService.fetchManagedPlaceId()).thenReturn(Observable.just(""))
        managePlaceInteractor.savePlaceData(Place(id = "1")).test()
                .assertValue(PartialManagePlaceViewState.SuccessfulUpdateState)
    }
}