package com.example.showmemovies.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.showmemovies.models.MediaIdMediaCategoryMapping

@Dao
interface MediaCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGenreIdsFromMovie(mediaCategoryMapping: List<MediaIdMediaCategoryMapping>)
}