package com.example.showmemovies.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.showmemovies.models.TvMediaIdGenreIdMapping
import com.example.showmemovies.models.TvModel

@Dao
interface TvIdGenreIdMappingDao {
    @Query("SELECT * FROM tv_media_id_genre_id_mapping WHERE tv_media_id=:tvId")
    suspend fun getGenreIdsForTv(tvId: Long): List<TvMediaIdGenreIdMapping>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGenreIdsFromTvMedia(tvMediaIdGenreIdMapping: List<TvMediaIdGenreIdMapping>)

    suspend fun saveGenreIdsFromTvMedia(mediaModel: TvModel) {
        val result = mutableListOf<TvMediaIdGenreIdMapping>()
        mediaModel.genreIds.forEach {
            result.add(
                TvMediaIdGenreIdMapping(
                    tvId = mediaModel.id,
                    genreId = it
                )
            )
        }
        saveGenreIdsFromTvMedia(result)
    }
}