package com.boardly.manageplace

import com.boardly.discover.models.Place

data class ManagePlaceViewState(val progress: Boolean = false,
                                val successfulUpdate: Boolean = false,
                                val managedPlace: Place = Place(),
                                val placeNameValid: Boolean = false,
                                val placeDescriptionValid: Boolean = false,
                                val placeLocationValid: Boolean = false,
                                val placeNumberValid: Boolean = false,
                                val render: Boolean = true,
                                val isPartner: Boolean = false)