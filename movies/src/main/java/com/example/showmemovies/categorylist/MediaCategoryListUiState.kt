package com.example.showmemovies.categorylist

import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.TvModelWithGenres

data class MediaCategoryListUiState(
    val movieMedia: List<MovieModelWithGenres> = listOf(),
    val movieMediaLastPageIndex: Int = 1,
    val tvMediaLastPageIndex: Int = 1,
    val tvMedia: List<TvModelWithGenres> = listOf(),
    val movieGenreIdMapping: Map<Long, String> = emptyMap(),
    val tvGenreIdMapping: Map<Long, String> = emptyMap(),
    val loadingMovieMedia: Boolean = true,
    val loadingTvMedia: Boolean = true,
    val errorMovieMedia: String = "",
    val errorTvMedia: String = ""
)