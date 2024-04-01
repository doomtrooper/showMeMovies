package com.example.showmemovies.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.showmemovies.models.MediaIdMediaCategoryMapping
import com.example.showmemovies.models.TvMediaIdMediaCategoryMapping

@Dao
interface TvMediaCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGenreIdsFromTvMedia(mediaCategoryMapping: List<TvMediaIdMediaCategoryMapping>)
}