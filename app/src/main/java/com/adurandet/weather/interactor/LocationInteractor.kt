package com.adurandet.weather.interactor

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.adurandet.weather.model.LOCATION_REQUEST_CODE
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class LocationInteractor(private val fragment: Fragment,
                         private val callback: Callback) {

    private val PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val client = LocationServices.getFusedLocationProviderClient(fragment.requireContext())

    fun verifyLocationPermissionsAndGetLocation() {
        val permission = ActivityCompat.checkSelfPermission(fragment.requireContext(), PERMISSION_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(arrayOf(PERMISSION_LOCATION), LOCATION_REQUEST_CODE)
            return
        }

        getLocation()
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            callback.onLocationPermissionsNotGranted()
            return
        }

        getLocation()
    }

    private fun getLocation() {
        client.lastLocation
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val latitude = task.result!!.latitude
                    val longitude = task.result!!.longitude
                    callback.onLocationReceived(LatLng(latitude, longitude))
                    return@addOnCompleteListener
                }

                callback.onLocationNotReceived()
            }
            .addOnFailureListener {
                callback.onLocationNotReceived()
            }
    }

    interface Callback {
        fun onLocationReceived(latLng: LatLng)
        fun onLocationPermissionsNotGranted()
        fun onLocationNotReceived()
    }
}