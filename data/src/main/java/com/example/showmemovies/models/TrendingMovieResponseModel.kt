package com.example.showmemovies.models

import com.squareup.moshi.Json

data class TrendingMoviesResponse(
    val page: Long,
    @field:Json(name = "results")
    val movieList: List<MovieModel>,
    @field:Json(name = "total_pages")
    val totalPages: Long,
    @field:Json(name = "total_results")
    val totalResults: Long,
)

data class MovieModel(
    val adult: Boolean,
    @field:Json(name = "backdrop_path")
    val backdropPath: String,
    val id: Long,
    val title: String,
    @field:Json(name = "original_language")
    val originalLanguage: String,
    @field:Json(name = "original_title")
    val originalTitle: String,
    val overview: String,
    @field:Json(name = "poster_path")
    val posterPath: String,
    @field:Json(name = "media_type")
    val mediaType: String,
    @field:Json(name = "genre_ids")
    val genreIds: List<Long>,
    val popularity: Double,
    @field:Json(name = "release_date")
    val releaseDate: String,
    val video: Boolean,
    @field:Json(name = "vote_average")
    val voteAverage: Double,
    @field:Json(name = "vote_count")
    val voteCount: Long,
)