package com.example.showmemovies.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.squareup.moshi.Json

data class TrendingMoviesResponse(
    val page: Int,
    @field:Json(name = "results")
    val movieList: List<MovieModel>,
    @field:Json(name = "total_pages")
    val totalPages: Int,
    @field:Json(name = "total_results")
    val totalResults: Int,
)

@Entity(tableName = "tending_movies")
data class MovieModel(
    val adult: Boolean,
    @field:Json(name = "backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String,
    @PrimaryKey val id: Long,
    val title: String,
    @ColumnInfo(name = "original_language")
    @field:Json(name = "original_language")
    val originalLanguage: String,
    @ColumnInfo(name = "original_title")
    @field:Json(name = "original_title")
    val originalTitle: String,
    val overview: String,
    @ColumnInfo(name = "poster_path")
    @field:Json(name = "poster_path")
    val posterPath: String,
    @ColumnInfo(name = "media_type")
    @field:Json(name = "media_type")
    val mediaType: String,
    val popularity: Double,
    @ColumnInfo(name = "release_date")
    @field:Json(name = "release_date")
    val releaseDate: String,
    val video: Boolean,
    @ColumnInfo(name = "vote_average")
    @field:Json(name = "vote_average")
    val voteAverage: Double,
    @ColumnInfo(name = "vote_count")
    @field:Json(name = "vote_count")
    val voteCount: Long,
) {
    @Ignore
    @field:Json(name = "genre_ids")
    val genreIds: List<Long> = listOf()
}

@Entity(tableName = "movie_id_genre_id_mapping", primaryKeys = ["movie_id", "genre_id"])
data class MovieIdGenreIdMapping(
    @ColumnInfo(name = "movie_id")
    val movieId: Long,
    @ColumnInfo(name = "genre_id")
    val genreId: Long
)

@Entity(tableName = "genre_name_id_mapping")
data class GenreNameIdMapping(
    @ColumnInfo(name = "genre_name")
    @field:Json(name = "name")
    val genreName: String,
    @PrimaryKey
    @ColumnInfo(name = "genre_id")
    @field:Json(name = "id")
    val genreId: Long
)

data class GenreNameIdMappingContainer(
    val genres: List<GenreNameIdMapping>
)

data class MovieModelWithGenres(
    @Embedded val movieModel: MovieModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "movie_id"
    )
    val genreIdMapping: List<MovieIdGenreIdMapping>
)