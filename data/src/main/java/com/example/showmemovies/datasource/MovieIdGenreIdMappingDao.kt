package com.example.showmemovies.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.showmemovies.models.MovieIdGenreIdMapping

@Dao
interface MovieIdGenreIdMappingDao {
    @Query("SELECT * FROM movie_id_genre_id_mapping WHERE movie_id=:movieId")
    suspend fun getGenreIdsForMovie(movieId: Long): List<MovieIdGenreIdMapping>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGenreIdsFroMovie(movieMovieIdGenreIdMapping: List<MovieIdGenreIdMapping>)
}