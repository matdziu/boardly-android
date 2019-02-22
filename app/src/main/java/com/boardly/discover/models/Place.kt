package com.boardly.discover.models

data class Place(val id: String = "",
                 val name: String = "",
                 val description: String = "",
                 val imageUrl: String = "",
                 val locationName: String = "",
                 val placeLatitude: Double = 0.0,
                 val placeLongitude: Double = 0.0,
                 val phoneNumber: String = "",
                 val pageLink: String = "")