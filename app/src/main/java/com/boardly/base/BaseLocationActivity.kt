package com.boardly.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.boardly.constants.LOCATION_SETTINGS_REQUEST_CODE
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.tbruyelle.rxpermissions2.RxPermissions

@SuppressLint("Registered")
open class BaseLocationActivity : AppCompatActivity() {

    private var locationGranted: () -> Unit = {}
    private var locationDenied: () -> Unit = {}

    private val locationRequest = LocationRequest().apply {
        interval = 1000
        fastestInterval = 1000
        numUpdates = 1
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = FusedLocationProviderClient(this)
    }

    fun checkLocationSettings(locationGranted: () -> Unit, locationDenied: () -> Unit) {
        this.locationGranted = locationGranted
        this.locationDenied = locationDenied

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener { checkLocationPermission() }
                .addOnFailureListener {
                    if (it is ResolvableApiException) {
                        it.startResolutionForResult(this, LOCATION_SETTINGS_REQUEST_CODE)
                    }
                }
    }

    private fun checkLocationPermission() {
        RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe {
                    if (!it) locationDenied()
                    else locationGranted()
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOCATION_SETTINGS_REQUEST_CODE -> handleLocationSettingsResult(resultCode)
        }
    }

    private fun handleLocationSettingsResult(resultCode: Int) {
        when (resultCode) {
            Activity.RESULT_OK -> checkLocationPermission()
            else -> locationDenied
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(onLocationFound: (location: Location) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) onLocationFound(it)
            else waitForLocation(onLocationFound)
        }
    }

    @SuppressLint("MissingPermission")
    private fun waitForLocation(onLocationFound: (location: Location) -> Unit) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                onLocationFound(locationResult.lastLocation)
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun isLocationPermissionGranted(): Boolean {
        return RxPermissions(this).isGranted(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}