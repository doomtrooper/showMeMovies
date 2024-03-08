package com.example.showmemovies

import com.example.showmemovies.models.MovieModel

data class MovieHomePageUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val trendingMovies: List<MovieModel> = emptyList(),
    val errorWrapper: ErrorWrapper? = null
)

data class ErrorWrapper(
    val t: Throwable? = null,
    val serviceErrorBody: ErrorBody? = null
)