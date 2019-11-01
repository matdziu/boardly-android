package com.boardly.retrofit.places.models

import com.google.gson.annotations.SerializedName

data class PlaceSearchResult(
        @SerializedName("display_name")
        val name: String,

        @SerializedName("lat")
        val latitude: Double,

        @SerializedName("lon")
        val longitude: Double)