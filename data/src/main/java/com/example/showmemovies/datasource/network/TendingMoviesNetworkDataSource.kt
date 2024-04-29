package com.example.showmemovies.datasource.network

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.MoviesApi
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.models.TvMediaResponseContainer
import javax.inject.Inject

interface ITendingMoviesNetworkDataSource {
    suspend fun fetchUpcomingMovies(page: Int = 1): NetworkResponseWrapper<MediaResponseContainer>
    suspend fun fetchTrendingMovies(page: Int = 1): NetworkResponseWrapper<MediaResponseContainer>
    suspend fun fetchTrendingTv(page: Int = 1): NetworkResponseWrapper<TvMediaResponseContainer>
    suspend fun fetchTopRatedMovie(page: Int = 1): NetworkResponseWrapper<MediaResponseContainer>
    suspend fun fetchTopRatedTv(page: Int = 1): NetworkResponseWrapper<TvMediaResponseContainer>
    suspend fun fetchPopularMovie(page: Int = 1): NetworkResponseWrapper<MediaResponseContainer>
    suspend fun fetchPopularTv(page: Int = 1): NetworkResponseWrapper<TvMediaResponseContainer>
}

class TendingMoviesNetworkDataSource @Inject constructor(private val moviesApi: MoviesApi) :
    ITendingMoviesNetworkDataSource {
    override suspend fun fetchUpcomingMovies(page: Int): NetworkResponseWrapper<MediaResponseContainer> {
        return moviesApi.fetchUpcomingMovies(page)
    }

    override suspend fun fetchTrendingMovies(page: Int): NetworkResponseWrapper<MediaResponseContainer> {
        return moviesApi.trendingMovie(page)
    }

    override suspend fun fetchTrendingTv(page: Int): NetworkResponseWrapper<TvMediaResponseContainer> {
        return moviesApi.trendingTv(page)
    }

    override suspend fun fetchTopRatedMovie(page: Int): NetworkResponseWrapper<MediaResponseContainer> {
        return moviesApi.topRatedMovie(page)
    }

    override suspend fun fetchTopRatedTv(page: Int): NetworkResponseWrapper<TvMediaResponseContainer> {
        return moviesApi.topRatedTv(page)
    }

    override suspend fun fetchPopularMovie(page: Int): NetworkResponseWrapper<MediaResponseContainer> {
        return moviesApi.popularMovie(page)
    }

    override suspend fun fetchPopularTv(page: Int): NetworkResponseWrapper<TvMediaResponseContainer> {
        return moviesApi.popularTv(page)
    }

}