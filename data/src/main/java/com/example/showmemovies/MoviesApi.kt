package com.example.showmemovies

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.models.GenreNameIdMappingContainer
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.models.TvGenreNameIdMappingContainer
import com.example.showmemovies.models.TvMediaResponseContainer
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {

    @GET("3/genre/movie/list")
    suspend fun movieGenre(): NetworkResponseWrapper<GenreNameIdMappingContainer>

    @GET("3/genre/tv/list")
    suspend fun tvGenre(): NetworkResponseWrapper<TvGenreNameIdMappingContainer>

    @GET("3/trending/movie/day?language=en-US")
    suspend fun trendingMovie(@Query("page") page: Int): NetworkResponseWrapper<MediaResponseContainer>

    @GET("3/trending/tv/day?language=en-US")
    suspend fun trendingTv(@Query("page") page: Int): NetworkResponseWrapper<TvMediaResponseContainer>


    @GET("3/movie/top_rated?language=en-US")
    suspend fun topRatedMovie(@Query("page") page: Int): NetworkResponseWrapper<MediaResponseContainer>

    @GET("3/tv/top_rated?language=en-US")
    suspend fun topRatedTv(@Query("page") page: Int): NetworkResponseWrapper<TvMediaResponseContainer>

    @GET("3/movie/popular?language=en-US")
    suspend fun popularMovie(@Query("page") page: Int): NetworkResponseWrapper<MediaResponseContainer>

    @GET("3/tv/popular?language=en-US")
    suspend fun popularTv(@Query("page") page: Int): NetworkResponseWrapper<TvMediaResponseContainer>

    @GET("3/movie/upcoming")
    suspend fun fetchUpcomingMovies(@Query("page") page: Int): NetworkResponseWrapper<MediaResponseContainer>


}