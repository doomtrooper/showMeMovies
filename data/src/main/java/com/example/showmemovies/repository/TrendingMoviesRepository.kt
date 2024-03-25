package com.example.showmemovies.repository

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.datasource.network.ITendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.dao.MovieIdGenreIdMappingDao
import com.example.showmemovies.datasource.dao.TrendingMovieDao
import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.MediaResponseContainer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ITrendingMoviesRepository {
    suspend fun flowTrendingMoviesFromDb(mediaCategory: MEDIACATEGORY = MEDIACATEGORY.TRENDING): Flow<List<MovieModelWithGenres>>
    suspend fun fetchTrendingMoviesFromNetwork(mediaCategory: MEDIACATEGORY = MEDIACATEGORY.TRENDING): NetworkResponseWrapper<MediaResponseContainer>
}

class TrendingMoviesRepository @Inject constructor(
    private val trendingMoviesNetworkDataSource: ITendingMoviesNetworkDataSource,
    private val trendingMovieDao: TrendingMovieDao,
    private val movieIdGenreIdMappingDao: MovieIdGenreIdMappingDao
) : ITrendingMoviesRepository {
    override suspend fun flowTrendingMoviesFromDb(mediaCategory: MEDIACATEGORY): Flow<List<MovieModelWithGenres>> {
        return trendingMovieDao.fetchTrendingMoviesWithGenre(mediaCategory)
    }

    override suspend fun fetchTrendingMoviesFromNetwork(mediaCategory: MEDIACATEGORY): NetworkResponseWrapper<MediaResponseContainer> {
        return trendingMoviesNetworkDataSource.fetchTrendingMedia().also {
            if (it is NetworkResponseWrapper.Success) {
                trendingMovieDao.updateNewTrendingMovies(it.body.movieList.map { movieModel ->
                    movieModel.copy(mediaCategory = mediaCategory)
                })
                it.body.movieList.forEach { movieModel ->
                    movieIdGenreIdMappingDao.saveGenreIdsFromMovie(
                        movieModel
                    )
                }
            }
        }
    }

}