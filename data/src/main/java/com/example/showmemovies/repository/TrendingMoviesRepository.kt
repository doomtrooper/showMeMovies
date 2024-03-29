package com.example.showmemovies.repository

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.datasource.network.ITendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.dao.MovieIdGenreIdMappingDao
import com.example.showmemovies.datasource.dao.TrendingMovieDao
import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.models.MEDIACATEGORY.*
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

interface ITrendingMoviesRepository {
    suspend fun flowTrendingMoviesFromDb(): Flow<Result<List<MovieModelWithGenres>, NetworkResponseWrapper<MediaResponseContainer>>>
    suspend fun fetchTrendingMoviesFromNetwork(mediaCategory: MEDIACATEGORY = TRENDING_ALL): NetworkResponseWrapper<MediaResponseContainer>

}

class TrendingMoviesRepository @Inject constructor(
    private val trendingMoviesNetworkDataSource: ITendingMoviesNetworkDataSource,
    private val trendingMovieDao: TrendingMovieDao,
    private val movieIdGenreIdMappingDao: MovieIdGenreIdMappingDao
) : ITrendingMoviesRepository {
    override suspend fun flowTrendingMoviesFromDb(): Flow<Result<List<MovieModelWithGenres>, NetworkResponseWrapper<MediaResponseContainer>>> {
        return merge(
            trendingMovieDao.fetchAllMoviesWithGenre().distinctUntilChanged().map { Result.Success(it) },
            flow {
                enumValues<MEDIACATEGORY>().forEach {
                    emit(fetchTrendingMoviesFromNetwork(it))
                }
            }.filter { it !is NetworkResponseWrapper.Success }.map {
                Result.Error(it)
            })
    }

    override suspend fun fetchTrendingMoviesFromNetwork(mediaCategory: MEDIACATEGORY): NetworkResponseWrapper<MediaResponseContainer> {
        return trendingMoviesNetworkDataSource.fetchTrendingMedia(mediaCategory).also {
            if (it is NetworkResponseWrapper.Success) {
                trendingMovieDao.updateNewTrendingMovies(it.body.movieList.map { movieModel ->
                    movieModel.copy(
                        mediaCategory = mediaCategory,
                        mediaType = movieModel.mediaType ?: when (mediaCategory) {
                            TRENDING_ALL, TOP_RATED_MOVIE, POPULAR_MOVIE -> "movie"
                            TOP_RATED_TV, POPULAR_TV -> "tv"
                        }
                    )
                }, mediaCategory)
                it.body.movieList.forEach { movieModel ->
                    movieIdGenreIdMappingDao.saveGenreIdsFromMovie(
                        movieModel
                    )
                }
            }
        }
    }

}