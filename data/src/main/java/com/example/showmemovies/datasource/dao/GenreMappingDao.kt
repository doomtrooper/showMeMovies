package com.example.showmemovies.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.showmemovies.models.GenreNameIdMapping
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreMappingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGenre(genres: List<GenreNameIdMapping>)

    @Query("SELECT * FROM genre_name_id_mapping")
    fun getAllGenre(): Flow<List<GenreNameIdMapping>>

    @Query("SELECT * FROM genre_name_id_mapping WHERE genre_id=:genreId")
    suspend fun getGenreForId(genreId: Long): GenreNameIdMapping
}