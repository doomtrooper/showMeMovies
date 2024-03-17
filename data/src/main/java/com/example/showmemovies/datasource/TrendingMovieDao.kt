package com.example.showmemovies.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.showmemovies.models.MovieModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingMovieDao {
    @Query("SELECT * FROM tending_movies")
    fun getAllTrendingMovies(): Flow<List<MovieModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllTrendingMovies(movies: List<MovieModel>)
}