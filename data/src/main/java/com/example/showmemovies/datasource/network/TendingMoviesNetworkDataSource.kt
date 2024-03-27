package com.example.showmemovies.datasource.network

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.MoviesApi
import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.models.MediaResponseContainer
import javax.inject.Inject

interface ITendingMoviesNetworkDataSource {
    suspend fun fetchTrendingMedia(mediaCategory: MEDIACATEGORY = MEDIACATEGORY.TRENDING_ALL): NetworkResponseWrapper<MediaResponseContainer>
}

class TendingMoviesNetworkDataSource @Inject constructor(private val moviesApi: MoviesApi) :
    ITendingMoviesNetworkDataSource {
    override suspend fun fetchTrendingMedia(mediaCategory: MEDIACATEGORY): NetworkResponseWrapper<MediaResponseContainer> {
        return when (mediaCategory) {
            MEDIACATEGORY.TRENDING_ALL -> moviesApi.trendingMedia()
            MEDIACATEGORY.TOP_RATED_MOVIE -> moviesApi.topRatedMovie()
            MEDIACATEGORY.TOP_RATED_TV -> moviesApi.topRatedTv()
            MEDIACATEGORY.POPULAR_TV -> moviesApi.popularTv()
            MEDIACATEGORY.POPULAR_MOVIE -> moviesApi.popularMovie()
        }
    }

}