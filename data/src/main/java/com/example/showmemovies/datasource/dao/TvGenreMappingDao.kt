package com.example.showmemovies.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.showmemovies.models.GenreNameIdMapping
import com.example.showmemovies.models.TvGenreNameIdMapping
import kotlinx.coroutines.flow.Flow

@Dao
interface TvGenreMappingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGenre(genres: List<TvGenreNameIdMapping>)

    @Query("SELECT * FROM tv_genre_name_id_mapping")
    fun getAllGenre(): Flow<List<TvGenreNameIdMapping>>

    @Query("SELECT * FROM tv_genre_name_id_mapping WHERE genre_id=:genreId")
    suspend fun getGenreForId(genreId: Long): GenreNameIdMapping
}