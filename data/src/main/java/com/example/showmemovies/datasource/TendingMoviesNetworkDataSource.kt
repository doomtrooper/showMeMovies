package com.example.showmemovies.datasource

import com.example.showmemovies.models.TrendingMoviesResponse
import javax.inject.Inject

interface ITendingMoviesNetworkDataSource {
    suspend fun fetchTrendingMovies(): TrendingMoviesResponse
}

class TendingMoviesNetworkDataSource @Inject constructor(private val moviesApi: MoviesApi) :
    ITendingMoviesNetworkDataSource {
    override suspend fun fetchTrendingMovies(): TrendingMoviesResponse {
       return moviesApi.trendingMovies()
    }

}