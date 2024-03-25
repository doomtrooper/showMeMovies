package com.example.showmemovies

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.models.GenreNameIdMappingContainer
import com.example.showmemovies.models.MediaResponseContainer
import retrofit2.http.GET

interface MoviesApi {
    @GET("3/trending/all/day?language=en-US")
    suspend fun trendingMedia(): NetworkResponseWrapper<MediaResponseContainer>


    @GET("3/genre/movie/list")
    suspend fun movieGenre(): NetworkResponseWrapper<GenreNameIdMappingContainer>

    @GET("3/movie/top_rated")
    suspend fun topRatedMovie(): NetworkResponseWrapper<MediaResponseContainer>

    @GET("3/tv/top_rated")
    suspend fun topRatedTv(): NetworkResponseWrapper<MediaResponseContainer>
}