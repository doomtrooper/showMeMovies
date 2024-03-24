package com.example.showmemovies.repository

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.datasource.network.ITendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.dao.MovieIdGenreIdMappingDao
import com.example.showmemovies.datasource.dao.TrendingMovieDao
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.TrendingMoviesResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ITrendingMoviesRepository {
    suspend fun flowTrendingMoviesFromDb(): Flow<List<MovieModelWithGenres>>
    suspend fun fetchTrendingMoviesFromNetwork(): NetworkResponseWrapper<TrendingMoviesResponse>
}

class TrendingMoviesRepository @Inject constructor(
    private val trendingMoviesNetworkDataSource: ITendingMoviesNetworkDataSource,
    private val trendingMovieDao: TrendingMovieDao,
    private val movieIdGenreIdMappingDao: MovieIdGenreIdMappingDao
) : ITrendingMoviesRepository {
    override suspend fun flowTrendingMoviesFromDb(): Flow<List<MovieModelWithGenres>> {
        return trendingMovieDao.fetchTrendingMoviesWithGenre()
    }

    override suspend fun fetchTrendingMoviesFromNetwork(): NetworkResponseWrapper<TrendingMoviesResponse> {
        return trendingMoviesNetworkDataSource.fetchTrendingMovies().also {
            if (it is NetworkResponseWrapper.Success) {
                trendingMovieDao.updateNewTrendingMovies(it.body.movieList)
                it.body.movieList.forEach { movieModel ->
                    movieIdGenreIdMappingDao.saveGenreIdsFromMovie(
                        movieModel
                    )
                }
            }
        }
    }

}