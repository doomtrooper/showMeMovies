package com.example.showmemovies

import com.example.showmemovies.datasource.dao.LocationDao
import com.example.showmemovies.models.LocationModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class MyLocationCallback(private val locationDao: LocationDao): LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult) {
        for (location in locationResult.locations) {
            println(location.toString())
            locationDao.insertLocation(
                LocationModel(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )
        }
    }
}