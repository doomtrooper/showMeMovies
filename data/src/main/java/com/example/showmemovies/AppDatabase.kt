package com.example.showmemovies

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.showmemovies.datasource.dao.GenreMappingDao
import com.example.showmemovies.datasource.dao.MovieIdGenreIdMappingDao
import com.example.showmemovies.datasource.dao.TrendingMovieDao
import com.example.showmemovies.models.MovieIdGenreIdMapping
import com.example.showmemovies.models.GenreNameIdMapping
import com.example.showmemovies.models.MediaModel

@Database(entities = [MediaModel::class, MovieIdGenreIdMapping::class, GenreNameIdMapping::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trendingMoviesDao(): TrendingMovieDao
    abstract fun movieIdGenreIdMappingDao(): MovieIdGenreIdMappingDao
    abstract fun genreDao(): GenreMappingDao
}