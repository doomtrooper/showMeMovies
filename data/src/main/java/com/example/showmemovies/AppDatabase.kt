package com.example.showmemovies

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.showmemovies.datasource.dao.GenreMappingDao
import com.example.showmemovies.datasource.dao.LocationDao
import com.example.showmemovies.datasource.dao.MediaCategoryDao
import com.example.showmemovies.datasource.dao.MovieIdGenreIdMappingDao
import com.example.showmemovies.datasource.dao.MovieDao
import com.example.showmemovies.datasource.dao.TvDao
import com.example.showmemovies.datasource.dao.TvGenreMappingDao
import com.example.showmemovies.datasource.dao.TvIdGenreIdMappingDao
import com.example.showmemovies.datasource.dao.TvMediaCategoryDao
import com.example.showmemovies.models.MovieIdGenreIdMapping
import com.example.showmemovies.models.GenreNameIdMapping
import com.example.showmemovies.models.LocationModel
import com.example.showmemovies.models.MediaModel
import com.example.showmemovies.models.MediaIdMediaCategoryMapping
import com.example.showmemovies.models.TvGenreNameIdMapping
import com.example.showmemovies.models.TvMediaIdMediaCategoryMapping
import com.example.showmemovies.models.TvModel
import com.example.showmemovies.models.TvMediaIdGenreIdMapping

@Database(
    entities = [
        MediaModel::class,
        MovieIdGenreIdMapping::class,
        TvMediaIdGenreIdMapping::class,
        GenreNameIdMapping::class,
        TvGenreNameIdMapping::class,
        TvModel::class,
        MediaIdMediaCategoryMapping::class,
        TvMediaIdMediaCategoryMapping::class,
        LocationModel::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moviesDao(): MovieDao
    abstract fun tvDao(): TvDao
    abstract fun mediaCategoryDao(): MediaCategoryDao
    abstract fun tvMediaCategoryDao(): TvMediaCategoryDao
    abstract fun movieIdGenreIdMappingDao(): MovieIdGenreIdMappingDao
    abstract fun tvIdGenreIdMappingDao(): TvIdGenreIdMappingDao
    abstract fun genreDao(): GenreMappingDao
    abstract fun tvGenreDao(): TvGenreMappingDao
    abstract fun locationDao(): LocationDao
}