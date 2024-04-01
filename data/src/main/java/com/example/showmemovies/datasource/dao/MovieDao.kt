package com.example.showmemovies.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.showmemovies.models.MediaModel
import com.example.showmemovies.models.MovieModelWithGenres
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM media_model")
    fun getAllTrendingMovies(): Flow<List<MediaModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllTrendingMovies(movies: List<MediaModel>)

    @Query("DELETE FROM media_model")
    suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM media_model")
    fun fetchAllMoviesWithGenre(): Flow<List<MovieModelWithGenres>>
}