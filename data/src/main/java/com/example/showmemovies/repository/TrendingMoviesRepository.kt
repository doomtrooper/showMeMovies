package com.example.showmemovies.repository

import com.example.showmemovies.NetworkResponseWrapper
import com.example.showmemovies.datasource.ITendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.TrendingMovieDao
import com.example.showmemovies.models.TrendingMoviesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

interface ITrendingMoviesRepository {
    suspend fun fetchTrendingMovies(): Flow<NetworkResponseWrapper<TrendingMoviesResponse>>
}

class TrendingMoviesRepository @Inject constructor(
    private val trendingMoviesNetworkDataSource: ITendingMoviesNetworkDataSource,
    private val trendingMovieDao: TrendingMovieDao
) : ITrendingMoviesRepository {
    override suspend fun fetchTrendingMovies(): Flow<NetworkResponseWrapper<TrendingMoviesResponse>> {
        return merge(
            trendingMovieDao.getAllTrendingMovies().map {
                NetworkResponseWrapper.Success(
                    TrendingMoviesResponse(
                        page = 0,
                        movieList = it,
                        totalPages = 1,
                        totalResults = it.size
                    )
                )
            },
            flow { trendingMoviesNetworkDataSource.fetchTrendingMovies() }
        )
    }

}