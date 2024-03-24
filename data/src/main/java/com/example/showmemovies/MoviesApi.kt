package com.example.showmemovies

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.models.GenreNameIdMappingContainer
import com.example.showmemovies.models.TrendingMoviesResponse
import retrofit2.http.GET

interface MoviesApi {
    @GET("3/trending/movie/day?language=en-US")
    suspend fun trendingMovies(): NetworkResponseWrapper<TrendingMoviesResponse>

    @GET("3/genre/movie/list")
    suspend fun movieGenre(): NetworkResponseWrapper<GenreNameIdMappingContainer>
}