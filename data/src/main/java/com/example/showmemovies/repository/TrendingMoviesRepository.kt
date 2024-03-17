package com.example.showmemovies.repository

import com.example.showmemovies.NetworkResponseWrapper
import com.example.showmemovies.datasource.ITendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.TrendingMovieDao
import com.example.showmemovies.models.TrendingMoviesResponse
import javax.inject.Inject

interface ITrendingMoviesRepository {
    suspend fun fetchTrendingMovies(): NetworkResponseWrapper<TrendingMoviesResponse>
}

class TrendingMoviesRepository @Inject constructor(
    private val trendingMoviesNetworkDataSource: ITendingMoviesNetworkDataSource,
) : ITrendingMoviesRepository {
    override suspend fun fetchTrendingMovies(): NetworkResponseWrapper<TrendingMoviesResponse> {
        return trendingMoviesNetworkDataSource.fetchTrendingMovies()
    }

}