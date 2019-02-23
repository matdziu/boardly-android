package com.boardly.manageplace

import com.boardly.discover.models.Place

data class ManagePlaceViewState(val progress: Boolean = false,
                                val successfulUpdate: Boolean = false,
                                val managedPlace: Place = Place(),
                                val placeNameValid: Boolean = true,
                                val placeDescriptionValid: Boolean = true,
                                val placeLocationValid: Boolean = true,
                                val placeNumberValid: Boolean = true,
                                val render: Boolean = true,
                                val isPartner: Boolean = false)