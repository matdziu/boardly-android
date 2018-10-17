package com.boardly.common.location

import android.location.Location

class DistanceCalculator {

    fun distanceBetween(startLatitude: Double, startLongitude: Double,
                        endLatitude: Double, endLongitude: Double): Float {
        val startLocation = Location("").apply {
            latitude = startLatitude
            longitude = startLongitude
        }
        val endLocation = Location("").apply {
            latitude = endLatitude
            longitude = endLongitude
        }
        return startLocation.distanceTo(endLocation)
    }
}