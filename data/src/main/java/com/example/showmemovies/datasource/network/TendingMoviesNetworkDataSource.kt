package com.example.showmemovies.datasource.network

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.MoviesApi
import com.example.showmemovies.models.TrendingMoviesResponse
import javax.inject.Inject

interface ITendingMoviesNetworkDataSource {
    suspend fun fetchTrendingMovies(): NetworkResponseWrapper<TrendingMoviesResponse>
}

class TendingMoviesNetworkDataSource @Inject constructor(private val moviesApi: MoviesApi) :
    ITendingMoviesNetworkDataSource {
    override suspend fun fetchTrendingMovies(): NetworkResponseWrapper<TrendingMoviesResponse> {
       return moviesApi.trendingMovies()
    }

}