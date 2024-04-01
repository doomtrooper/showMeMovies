package com.example.showmemovies.datasource.network

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.MoviesApi
import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.models.TvMediaResponseContainer
import javax.inject.Inject

interface ITendingMoviesNetworkDataSource {
    suspend fun fetchUpcomingMovies(): NetworkResponseWrapper<MediaResponseContainer>
    suspend fun fetchTrendingMovies(): NetworkResponseWrapper<MediaResponseContainer>
    suspend fun fetchTrendingTv(): NetworkResponseWrapper<TvMediaResponseContainer>
    suspend fun fetchTopRatedMovie(): NetworkResponseWrapper<MediaResponseContainer>
    suspend fun fetchTopRatedTv(): NetworkResponseWrapper<TvMediaResponseContainer>
    suspend fun fetchPopularMovie(): NetworkResponseWrapper<MediaResponseContainer>
    suspend fun fetchPopularTv(): NetworkResponseWrapper<TvMediaResponseContainer>
}

class TendingMoviesNetworkDataSource @Inject constructor(private val moviesApi: MoviesApi) :
    ITendingMoviesNetworkDataSource {
    override suspend fun fetchUpcomingMovies(): NetworkResponseWrapper<MediaResponseContainer> {
        return moviesApi.fetchUpcomingMovies()
    }

    override suspend fun fetchTrendingMovies(): NetworkResponseWrapper<MediaResponseContainer> {
        return moviesApi.trendingMovie()
    }

    override suspend fun fetchTrendingTv(): NetworkResponseWrapper<TvMediaResponseContainer> {
        return moviesApi.trendingTv()
    }

    override suspend fun fetchTopRatedMovie(): NetworkResponseWrapper<MediaResponseContainer> {
        return moviesApi.topRatedMovie()
    }

    override suspend fun fetchTopRatedTv(): NetworkResponseWrapper<TvMediaResponseContainer> {
        return moviesApi.topRatedTv()
    }

    override suspend fun fetchPopularMovie(): NetworkResponseWrapper<MediaResponseContainer> {
        return moviesApi.popularMovie()
    }

    override suspend fun fetchPopularTv(): NetworkResponseWrapper<TvMediaResponseContainer> {
        return moviesApi.popularTv()
    }

}