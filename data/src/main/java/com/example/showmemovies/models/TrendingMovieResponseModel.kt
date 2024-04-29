package com.example.showmemovies.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.gson.annotations.SerializedName

data class MediaResponseContainer(
    val page: Int = 0,
    @SerializedName("results")
    val movieList: List<MediaModel>,
    @SerializedName("total_pages")
    val totalPages: Int = 0,
    @SerializedName("total_results")
    val totalResults: Int = 0,
)


@Entity(tableName = "media_model")
data class MediaModel(
    val adult: Boolean,

    @SerializedName("backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String,

    @PrimaryKey val id: Long,

    @SerializedName(value = "title", alternate = ["name"])
    val title: String,

    @ColumnInfo(name = "original_language")
    @SerializedName("original_language")
    val originalLanguage: String,

    @ColumnInfo(name = "original_title")
    @SerializedName(value = "original_title", alternate = ["original_name"])
    val originalTitle: String,

    val overview: String,

    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    val posterPath: String,

    val popularity: Double,

    @ColumnInfo(name = "release_date")
    @SerializedName("release_date", alternate = ["first_air_date"])
    val releaseDate: String,

    val video: Boolean,

    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    val voteAverage: Double,

    @ColumnInfo(name = "vote_count")
    @SerializedName("vote_count")
    val voteCount: Long,

    @ColumnInfo(name = "page")
    val page: Int = 1,

) {
    @Ignore
    @SerializedName("genre_ids")
    var genreIds: List<Long> = listOf()
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
    @SerializedName("name")
    val genreName: String,
    @PrimaryKey
    @ColumnInfo(name = "genre_id")
    @SerializedName("id")
    val genreId: Long
)



data class GenreNameIdMappingContainer(
    val genres: List<GenreNameIdMapping>
)

data class
MovieModelWithGenres(
    @Embedded val mediaModel: MediaModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "movie_id"
    )
    val genreIdMapping: List<MovieIdGenreIdMapping>,
    @Relation(
        parentColumn = "id",
        entityColumn = "movie_id"
    )
    val mediaCategoryMapping: List<MediaIdMediaCategoryMapping>
)

@Entity(tableName = "media_id_category_mapping", primaryKeys = ["movie_id", "media_category"])
data class MediaIdMediaCategoryMapping(
    @ColumnInfo(name = "movie_id")
    val movieId: Long,
    @ColumnInfo(name = "media_category")
    val category: MEDIACATEGORY
)

enum class MEDIACATEGORY {
    TRENDING_MOVIE,
    TOP_RATED_MOVIE,
    POPULAR_MOVIE,
    UPCOMING_MOVIE,
}

enum class TVMEDIACATEGORY{
    TRENDING_TV,POPULAR_TV,TOP_RATED_TV,
}