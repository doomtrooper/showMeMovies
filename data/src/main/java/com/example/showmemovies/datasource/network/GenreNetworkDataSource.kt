package com.example.showmemovies.datasource.network

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.MoviesApi
import com.example.showmemovies.models.GenreNameIdMappingContainer
import javax.inject.Inject

interface IGenreNetworkDataSource {
    suspend fun fetchMovieGenres(): NetworkResponseWrapper<GenreNameIdMappingContainer>
}

class GenreNetworkDataSource @Inject constructor(private val moviesApi: MoviesApi) :
    IGenreNetworkDataSource {
    override suspend fun fetchMovieGenres(): NetworkResponseWrapper<GenreNameIdMappingContainer> {
        return moviesApi.movieGenre()
    }

}