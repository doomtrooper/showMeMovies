package com.example.showmemovies.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.showmemovies.models.TvModel
import com.example.showmemovies.models.TvModelWithGenres
import kotlinx.coroutines.flow.Flow


@Dao
interface TvDao {
    @Query("SELECT * FROM tv_model")
    fun getAllTrendingTvMedia(): Flow<List<TvModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllTrendingTvMedia(movies: List<TvModel>)

    @Query("DELETE FROM tv_model")
    suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM tv_model")
    fun fetchAllTvMediaWithGenre(): Flow<List<TvModelWithGenres>>
}