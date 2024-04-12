package com.example.showmemovies.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.showmemovies.models.LocationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(locations: LocationModel)
    @Query("SELECT * FROM location")
    fun fetchLatestLocation(): Flow<LocationModel>
}