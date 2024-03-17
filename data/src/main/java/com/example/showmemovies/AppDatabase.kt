package com.example.showmemovies

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.showmemovies.datasource.GenreMappingDao
import com.example.showmemovies.datasource.MovieIdGenreIdMappingDao
import com.example.showmemovies.datasource.TrendingMovieDao
import com.example.showmemovies.models.MovieIdGenreIdMapping
import com.example.showmemovies.models.GenreNameIdMapping
import com.example.showmemovies.models.MovieModel

@Database(entities = [MovieModel::class, MovieIdGenreIdMapping::class, GenreNameIdMapping::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trendingMoviesDao(): TrendingMovieDao
    abstract fun movieIdGenreIdMappingDao(): MovieIdGenreIdMappingDao
    abstract fun genreDao(): GenreMappingDao
}