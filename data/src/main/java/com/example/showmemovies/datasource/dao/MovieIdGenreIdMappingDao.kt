package com.example.showmemovies.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.showmemovies.models.MovieIdGenreIdMapping
import com.example.showmemovies.models.MediaModel

@Dao
interface MovieIdGenreIdMappingDao {
    @Query("SELECT * FROM movie_id_genre_id_mapping WHERE movie_id=:movieId")
    suspend fun getGenreIdsForMovie(movieId: Long): List<MovieIdGenreIdMapping>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGenreIdsFromMovie(movieMovieIdGenreIdMapping: List<MovieIdGenreIdMapping>)

    suspend fun saveGenreIdsFromMovie(mediaModel: MediaModel) {
        val result = mutableListOf<MovieIdGenreIdMapping>()
        mediaModel.genreIds.forEach {
            result.add(
                MovieIdGenreIdMapping(
                    movieId = mediaModel.id,
                    genreId = it
                )
            )
        }
        saveGenreIdsFromMovie(result)
    }
}