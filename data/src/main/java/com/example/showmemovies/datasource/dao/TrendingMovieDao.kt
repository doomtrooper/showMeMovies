package com.example.showmemovies.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.showmemovies.models.MovieModel
import com.example.showmemovies.models.MovieModelWithGenres
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingMovieDao {
    @Query("SELECT * FROM tending_movies")
    fun getAllTrendingMovies(): Flow<List<MovieModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllTrendingMovies(movies: List<MovieModel>)

    @Query("DELETE FROM tending_movies")
    suspend fun deleteAll()

    @Transaction
    suspend fun updateNewTrendingMovies(movies: List<MovieModel>){
        deleteAll()
        saveAllTrendingMovies(movies)
    }

    @Transaction
    @Query("SELECT * FROM tending_movies")
    fun fetchTrendingMoviesWithGenre(): Flow<List<MovieModelWithGenres>>
}