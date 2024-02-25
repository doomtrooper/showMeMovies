package com.example.showmemovies.repository

import com.example.showmemovies.datasource.ITendingMoviesNetworkDataSource
import javax.inject.Inject

interface ITrendingMoviesRepository {
    suspend fun fetchTrendingMovies()
}

class TrendingMoviesRepository @Inject constructor(private val trendingMoviesNetworkDataSource: ITendingMoviesNetworkDataSource): ITrendingMoviesRepository{
    override suspend fun fetchTrendingMovies() {
        return trendingMoviesNetworkDataSource.fetchTrendingMovies()
    }

}