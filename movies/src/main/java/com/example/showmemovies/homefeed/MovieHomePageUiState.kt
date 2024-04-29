package com.example.showmemovies.homefeed

import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.TvModelWithGenres
import com.example.showmemovies.utils.ErrorBody

data class MovieHomePageUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val upcomingMovies: List<MovieModelWithGenres> = emptyList(),
    val trendingMovies: List<MovieModelWithGenres> = emptyList(),
    val topRatedMovies: List<MovieModelWithGenres> = emptyList(),
    val popularMovies: List<MovieModelWithGenres> = emptyList(),
    val topRatedTv: List<TvModelWithGenres> = emptyList(),
    val popularTv: List<TvModelWithGenres> = emptyList(),
    val trendingTv: List<TvModelWithGenres> = emptyList(),
    val errorWrapper: ErrorWrapper? = null,
    val genreIdMapping: Map<Long, String> = emptyMap(),
    val tvGenreIdMapping: Map<Long, String> = emptyMap()
)

data class ErrorWrapper(
    val t: Throwable? = null,
    val serviceErrorBody: ErrorBody? = null
)