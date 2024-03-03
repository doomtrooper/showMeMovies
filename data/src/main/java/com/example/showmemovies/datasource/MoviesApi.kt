package com.example.showmemovies.datasource

import com.example.showmemovies.NetworkResponseWrapper
import com.example.showmemovies.models.TrendingMoviesResponse
import retrofit2.http.GET

interface MoviesApi {
    @GET("3/trending/movie/day?language=en-US")
    suspend fun trendingMovies() : NetworkResponseWrapper<TrendingMoviesResponse>
}