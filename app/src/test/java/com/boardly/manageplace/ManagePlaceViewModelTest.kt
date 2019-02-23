package com.boardly.manageplace

import com.boardly.discover.models.Place
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class ManagePlaceViewModelTest {

    private val managePlaceInteractor: ManagePlaceInteractor = mock()
    private val managePlaceViewModel = ManagePlaceViewModel(managePlaceInteractor)
    private val managePlaceViewRobot = ManagePlaceViewRobot(managePlaceViewModel)

    @Test
    fun testSuccessfulPlaceDataFetching() {
        whenever(managePlaceInteractor.fetchPlaceData()).thenReturn(Observable.just(PartialManagePlaceViewState.PlaceDataFetched(Place(id = "1"), render = true)))
        managePlaceViewRobot.emitFetchTrigger()
        managePlaceViewRobot.assertViewStates(
                ManagePlaceViewState(),
                ManagePlaceViewState(progress = true),
                ManagePlaceViewState(managedPlace = Place(id = "1")))
    }

    @Test
    fun testPlaceDataFetchingForNonPartner() {
        whenever(managePlaceInteractor.fetchPlaceData()).thenReturn(Observable.just(PartialManagePlaceViewState.PartnershipCheckState(false)))
        managePlaceViewRobot.emitFetchTrigger()
        managePlaceViewRobot.assertViewStates(
                ManagePlaceViewState(),
                ManagePlaceViewState(progress = true),
                ManagePlaceViewState(isPartner = false))
    }

    @Test
    fun testSuccessfulPlaceSaving() {
        whenever(managePlaceInteractor.savePlaceData(any())).thenReturn(Observable.just(PartialManagePlaceViewState.SuccessfulUpdateState))
        managePlaceViewRobot.emitPlace(Place(
                id = "1",
                adminId = "1",
                name = "1",
                description = "1",
                imageUrl = "1",
                locationName = "1",
                phoneNumber = "1"))
        managePlaceViewRobot.assertViewStates(
                ManagePlaceViewState(),
                ManagePlaceViewState(
                        progress = true),
                ManagePlaceViewState(
                        progress = true,
                        successfulUpdate = true))
    }

    @Test
    fun testLocalPlaceDataValidation() {
        whenever(managePlaceInteractor.savePlaceData(any())).thenReturn(Observable.just(PartialManagePlaceViewState.SuccessfulUpdateState))
        managePlaceViewRobot.emitPlace(Place(
                id = "1",
                adminId = "1",
                description = "1",
                imageUrl = "1",
                locationName = "1",
                phoneNumber = "1"))
        managePlaceViewRobot.emitPlace(Place(
                id = "1",
                adminId = "1",
                name = "1",
                imageUrl = "1",
                locationName = "1",
                phoneNumber = "1"))
        managePlaceViewRobot.emitPlace(Place(
                id = "1",
                adminId = "1",
                name = "1",
                description = "1",
                imageUrl = "1",
                locationName = "1"))
        managePlaceViewRobot.assertViewStates(
                ManagePlaceViewState(),
                ManagePlaceViewState(placeNameValid = false),
                ManagePlaceViewState(placeDescriptionValid = false),
                ManagePlaceViewState(placeNumberValid = false))
    }

    @Test
    fun testPlacePickEvent() {
        managePlaceViewRobot.emitPlace(Place(
                id = "1",
                adminId = "1",
                name = "1",
                description = "1",
                imageUrl = "1"))
        managePlaceViewRobot.emitPlacePickEvent()
        managePlaceViewRobot.assertViewStates(
                ManagePlaceViewState(),
                ManagePlaceViewState(placeNumberValid = false, placeLocationValid = false),
                ManagePlaceViewState(placeNumberValid = false))
    }
}