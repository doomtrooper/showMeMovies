package com.example.showmemovies

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.models.GenreNameIdMappingContainer
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.models.TvGenreNameIdMappingContainer
import com.example.showmemovies.models.TvMediaResponseContainer
import retrofit2.http.GET

interface MoviesApi {

    @GET("3/genre/movie/list")
    suspend fun movieGenre(): NetworkResponseWrapper<GenreNameIdMappingContainer>

    @GET("3/genre/tv/list")
    suspend fun tvGenre(): NetworkResponseWrapper<TvGenreNameIdMappingContainer>

    @GET("3/trending/movie/day?language=en-US")
    suspend fun trendingMovie(): NetworkResponseWrapper<MediaResponseContainer>

    @GET("3/trending/tv/day?language=en-US")
    suspend fun trendingTv(): NetworkResponseWrapper<TvMediaResponseContainer>


    @GET("3/movie/top_rated?language=en-US")
    suspend fun topRatedMovie(): NetworkResponseWrapper<MediaResponseContainer>

    @GET("3/tv/top_rated?language=en-US")
    suspend fun topRatedTv(): NetworkResponseWrapper<TvMediaResponseContainer>

    @GET("3/movie/popular?language=en-US")
    suspend fun popularMovie(): NetworkResponseWrapper<MediaResponseContainer>

    @GET("3/tv/popular?language=en-US")
    suspend fun popularTv(): NetworkResponseWrapper<TvMediaResponseContainer>

    @GET("3/movie/upcoming")
    suspend fun fetchUpcomingMovies(): NetworkResponseWrapper<MediaResponseContainer>


}