package com.example.showmemovies.datasource

import com.example.showmemovies.NetworkResponseWrapper
import com.example.showmemovies.models.TrendingMoviesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ITendingMoviesNetworkDataSource {
    suspend fun fetchTrendingMovies(): Flow<NetworkResponseWrapper<TrendingMoviesResponse>>
}

class TendingMoviesNetworkDataSource @Inject constructor(private val moviesApi: MoviesApi) :
    ITendingMoviesNetworkDataSource {
    override suspend fun fetchTrendingMovies(): Flow<NetworkResponseWrapper<TrendingMoviesResponse>> {
       return flow {  emit(moviesApi.trendingMovies()) }
    }

}