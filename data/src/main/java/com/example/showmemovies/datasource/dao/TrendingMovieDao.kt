package com.example.showmemovies.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.models.MediaModel
import com.example.showmemovies.models.MovieModelWithGenres
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendingMovieDao {
    @Query("SELECT * FROM media_model")
    fun getAllTrendingMovies(): Flow<List<MediaModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllTrendingMovies(movies: List<MediaModel>)

    @Query("DELETE FROM media_model where media_category=:mediaCategory")
    suspend fun deleteAll(mediaCategory: MEDIACATEGORY)

    @Transaction
    suspend fun updateNewTrendingMovies(
        movies: List<MediaModel>,
        mediaCategory: MEDIACATEGORY
    ){
        deleteAll(mediaCategory)
        saveAllTrendingMovies(movies)
    }

    @Transaction
    @Query("SELECT * FROM media_model where media_category=:mediaCategory")
    fun fetchTrendingMoviesWithGenre(mediaCategory: MEDIACATEGORY): Flow<List<MovieModelWithGenres>>

    @Transaction
    @Query("SELECT * FROM media_model")
    fun fetchAllMoviesWithGenre(): Flow<List<MovieModelWithGenres>>
}