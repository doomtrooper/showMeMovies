package com.example.showmemovies.locationservice

import com.example.showmemovies.datasource.dao.LocationDao
import com.example.showmemovies.models.LocationModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class MyLocationCallback(private val onLocation: (locationResult: LocationResult) -> Unit) :
    LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult) {
        this.onLocation(locationResult)
    }
}