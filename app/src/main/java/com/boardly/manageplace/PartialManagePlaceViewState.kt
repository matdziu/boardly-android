package com.boardly.manageplace

import com.boardly.discover.models.Place

sealed class PartialManagePlaceViewState {

    abstract fun reduce(previousState: ManagePlaceViewState): ManagePlaceViewState

    object ProgressState : PartialManagePlaceViewState() {
        override fun reduce(previousState: ManagePlaceViewState): ManagePlaceViewState {
            return ManagePlaceViewState(progress = true)
        }
    }

    object SuccessfulUpdateState : PartialManagePlaceViewState() {
        override fun reduce(previousState: ManagePlaceViewState): ManagePlaceViewState {
            return ManagePlaceViewState(successfulUpdate = true)
        }
    }

    data class LocalValidation(private val placeNameValid: Boolean = true,
                               private val placeDescriptionValid: Boolean = true,
                               private val placeLocationValid: Boolean = true,
                               private val placeNumberValid: Boolean = true) : PartialManagePlaceViewState() {
        override fun reduce(previousState: ManagePlaceViewState): ManagePlaceViewState {
            return previousState.copy(
                    placeNameValid = placeNameValid,
                    placeDescriptionValid = placeDescriptionValid,
                    placeLocationValid = placeLocationValid,
                    placeNumberValid = placeNumberValid)
        }
    }

    data class PartnershipCheckState(private val isPartner: Boolean = false) : PartialManagePlaceViewState() {
        override fun reduce(previousState: ManagePlaceViewState): ManagePlaceViewState {
            return previousState.copy(
                    progress = false,
                    isPartner = isPartner)
        }
    }

    data class PlaceDataFetched(private val place: Place,
                                private val render: Boolean = false) : PartialManagePlaceViewState() {
        override fun reduce(previousState: ManagePlaceViewState): ManagePlaceViewState {
            return previousState.copy(
                    progress = false,
                    render = render,
                    managedPlace = place)
        }
    }
}