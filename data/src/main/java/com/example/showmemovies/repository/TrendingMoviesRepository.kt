package com.example.showmemovies.repository

import com.example.showmemovies.datasource.ITendingMoviesNetworkDataSource
import com.example.showmemovies.models.TrendingMoviesResponse
import javax.inject.Inject

interface ITrendingMoviesRepository {
    suspend fun fetchTrendingMovies(): TrendingMoviesResponse
}

class TrendingMoviesRepository @Inject constructor(private val trendingMoviesNetworkDataSource: ITendingMoviesNetworkDataSource): ITrendingMoviesRepository{
    override suspend fun fetchTrendingMovies(): TrendingMoviesResponse {
        return trendingMoviesNetworkDataSource.fetchTrendingMovies()
    }

}