package com.example.showmemovies

import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.utils.ErrorBody

data class MovieHomePageUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val trendingMovies: List<MovieModelWithGenres> = emptyList(),
    val errorWrapper: ErrorWrapper? = null,
    val genreIdMapping: Map<Long, String> = emptyMap()
)

data class ErrorWrapper(
    val t: Throwable? = null,
    val serviceErrorBody: ErrorBody? = null
)