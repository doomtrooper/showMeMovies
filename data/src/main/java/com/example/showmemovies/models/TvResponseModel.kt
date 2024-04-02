package com.example.showmemovies.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.gson.annotations.SerializedName

data class TvMediaResponseContainer(
    val page: Int = 0,
    @SerializedName("results")
    val tvModelList: List<TvModel>,
    @SerializedName("total_pages")
    val totalPages: Int = 0,
    @SerializedName("total_results")
    val totalResults: Int = 0,
)

@Entity(tableName = "tv_genre_name_id_mapping")
data class TvGenreNameIdMapping(
    @ColumnInfo(name = "genre_name")
    @SerializedName("name")
    val genreName: String,
    @PrimaryKey
    @ColumnInfo(name = "genre_id")
    @SerializedName("id")
    val genreId: Long
)


data class TvGenreNameIdMappingContainer(
    val genres: List<TvGenreNameIdMapping>
)


@Entity(tableName = "tv_media_id_category_mapping", primaryKeys = ["tv_id", "media_category"])
data class TvMediaIdMediaCategoryMapping(
    @ColumnInfo(name = "tv_id")
    val tvId: Long,
    @ColumnInfo(name = "media_category")
    val category: TVMEDIACATEGORY
)

@Entity(tableName = "tv_media_id_genre_id_mapping", primaryKeys = ["tv_media_id", "genre_id"])
data class TvMediaIdGenreIdMapping(
    @ColumnInfo(name = "tv_media_id")
    val tvId: Long,
    @ColumnInfo(name = "genre_id")
    val genreId: Long
)


data class TvModelWithGenres(
    @Embedded val mediaModel: TvModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "tv_media_id"
    )
    val genreIdMapping: List<TvMediaIdGenreIdMapping>,
    @Relation(
        parentColumn = "id",
        entityColumn = "tv_id"
    )
    val mediaCategoryMapping: List<TvMediaIdMediaCategoryMapping>
)

@Entity(tableName = "tv_model")
data class TvModel(
    val adult: Boolean,

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

    ) {
    @Ignore
    @SerializedName("genre_ids")
    val genreIds: List<Long> = listOf()

    @SerializedName("backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    var backdropPath: String? = null
}