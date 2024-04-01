package com.example.showmemovies.datasource.network

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.MoviesApi
import com.example.showmemovies.models.GenreNameIdMappingContainer
import com.example.showmemovies.models.TvGenreNameIdMappingContainer
import javax.inject.Inject

interface ITvGenreNetworkDataSource {
    suspend fun fetchTvMovieGenres(): NetworkResponseWrapper<TvGenreNameIdMappingContainer>
}

class TvGenreNetworkDataSource @Inject constructor(private val moviesApi: MoviesApi) :
    ITvGenreNetworkDataSource {
    override suspend fun fetchTvMovieGenres(): NetworkResponseWrapper<TvGenreNameIdMappingContainer> {
        return moviesApi.tvGenre()
    }

}