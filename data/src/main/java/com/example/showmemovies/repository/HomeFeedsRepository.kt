package com.example.showmemovies.repository

import com.example.showmemovies.FeedApiMapper
import com.example.showmemovies.datasource.dao.MediaCategoryDao
import com.example.showmemovies.datasource.dao.MovieDao
import com.example.showmemovies.datasource.dao.MovieIdGenreIdMappingDao
import com.example.showmemovies.datasource.dao.TvDao
import com.example.showmemovies.datasource.dao.TvIdGenreIdMappingDao
import com.example.showmemovies.datasource.dao.TvMediaCategoryDao
import com.example.showmemovies.datasource.network.ITendingMoviesNetworkDataSource
import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.models.MediaIdMediaCategoryMapping
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.TVMEDIACATEGORY
import com.example.showmemovies.models.TvMediaIdMediaCategoryMapping
import com.example.showmemovies.models.TvMediaResponseContainer
import com.example.showmemovies.models.TvModelWithGenres
import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

interface IHomeFeedsRepository {
    suspend fun flowTrendingMoviesFromDb(): Flow<Result<List<MovieModelWithGenres>, NetworkResponseWrapper<MediaResponseContainer>>>
    suspend fun flowTvMediaFromDb(): Flow<Result<List<TvModelWithGenres>, NetworkResponseWrapper<TvMediaResponseContainer>>>

}

class HomeFeedsRepository @Inject constructor(
    private val trendingMoviesNetworkDataSource: ITendingMoviesNetworkDataSource,
    private val movieDao: MovieDao,
    private val tvDao: TvDao,
    private val mediaCategoryDao: MediaCategoryDao,
    private val tvMediaCategoryDao: TvMediaCategoryDao,
    private val movieIdGenreIdMappingDao: MovieIdGenreIdMappingDao,
    private val tvIdGenreIdMappingDao: TvIdGenreIdMappingDao,
    private val feedApiMapper: FeedApiMapper,
) : IHomeFeedsRepository {
    override suspend fun flowTrendingMoviesFromDb(): Flow<Result<List<MovieModelWithGenres>, NetworkResponseWrapper<MediaResponseContainer>>> {
        return merge(
            movieDao.fetchAllMoviesWithGenre().distinctUntilChanged().map { Result.Success(it) },
            flow {
                enumValues<MEDIACATEGORY>().forEach {
                    emit(fetchTrendingMoviesFromNetwork(it))
                }
            }.filter { it !is NetworkResponseWrapper.Success }.map {
                Result.Error(it)
            }
        )
    }

    override suspend fun flowTvMediaFromDb(): Flow<Result<List<TvModelWithGenres>, NetworkResponseWrapper<TvMediaResponseContainer>>> {
        return merge(
            tvDao.fetchAllTvMediaWithGenre().distinctUntilChanged().map { Result.Success(it) },
            flow {
                enumValues<TVMEDIACATEGORY>().forEach {
                    emit(fetchTrendingTvFromNetwork(it))
                }
            }.filter { it !is NetworkResponseWrapper.Success }.map {
                Result.Error(it)
            }
        )
    }


    private suspend fun fetchTrendingMoviesFromNetwork(mediaCategory: MEDIACATEGORY): NetworkResponseWrapper<MediaResponseContainer> {
        return feedApiMapper.feedMovieMediaApiMapper.getOrDefault(
            mediaCategory,
            suspend { trendingMoviesNetworkDataSource.fetchTrendingMovies() }).invoke().also {
            if (it is NetworkResponseWrapper.Success) {
                movieDao.saveAllTrendingMovies(it.body.movieList)
                mediaCategoryDao.saveGenreIdsFromMovie(it.body.movieList.map { mediaModel ->
                    MediaIdMediaCategoryMapping(
                        mediaModel.id,
                        mediaCategory
                    )
                })
                it.body.movieList.forEach { movieModel ->
                    movieIdGenreIdMappingDao.saveGenreIdsFromMovie(
                        movieModel
                    )
                }
            }
        }
    }

    private suspend fun fetchTrendingTvFromNetwork(mediaCategory: TVMEDIACATEGORY): NetworkResponseWrapper<TvMediaResponseContainer> {
        return feedApiMapper.feedTvMediaApiMapper.getOrDefault(
            mediaCategory,
            suspend { trendingMoviesNetworkDataSource.fetchTrendingTv() }).invoke().also {
            if (it is NetworkResponseWrapper.Success) {
                tvDao.saveAllTrendingTvMedia(it.body.movieList)
                tvMediaCategoryDao.saveGenreIdsFromTvMedia(it.body.movieList.map { mediaModel ->
                    TvMediaIdMediaCategoryMapping(
                        mediaModel.id,
                        mediaCategory
                    )
                })
                it.body.movieList.forEach { movieModel ->
                    tvIdGenreIdMappingDao.saveGenreIdsFromTvMedia(
                        movieModel
                    )
                }
            }
        }
    }

}