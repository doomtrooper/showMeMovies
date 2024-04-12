package com.example.showmemovies.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationModel(
    @PrimaryKey var id: Int = 1,
    var latitude: Double,
    var longitude: Double,
)