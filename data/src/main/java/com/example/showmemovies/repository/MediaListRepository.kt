package com.example.showmemovies.repository

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
import com.example.showmemovies.utils.FeedApiMapper
import com.example.showmemovies.utils.NetworkResponseWrapper
import kotlinx.coroutines.flow.Flow
import com.example.showmemovies.utils.Result
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

interface IMediaListRepository {
    fun flowMoviesFromDb(
        mediaCategory: MEDIACATEGORY
    ): Flow<Result<List<MovieModelWithGenres>, NetworkResponseWrapper<MediaResponseContainer>>>

    fun flowTvMediaFromDb(
        tvMediaCategory: TVMEDIACATEGORY,
        page: Int = 1
    ): Flow<Result<List<TvModelWithGenres>, NetworkResponseWrapper<TvMediaResponseContainer>>>

    suspend fun fetchTrendingMoviesFromNetwork(
        mediaCategory: MEDIACATEGORY,
        page: Int = 1
    ): NetworkResponseWrapper<MediaResponseContainer>
}

class MediaListRepository @Inject constructor(
    private val trendingMoviesNetworkDataSource: ITendingMoviesNetworkDataSource,
    private val movieDao: MovieDao,
    private val tvDao: TvDao,
    private val mediaCategoryDao: MediaCategoryDao,
    private val tvMediaCategoryDao: TvMediaCategoryDao,
    private val movieIdGenreIdMappingDao: MovieIdGenreIdMappingDao,
    private val tvIdGenreIdMappingDao: TvIdGenreIdMappingDao,
    private val feedApiMapper: FeedApiMapper,
) : IMediaListRepository {
    override fun flowMoviesFromDb(
        mediaCategory: MEDIACATEGORY
    ): Flow<Result<List<MovieModelWithGenres>, NetworkResponseWrapper<MediaResponseContainer>>> {
        return merge(
            movieDao.getAllMovies()
                .map {
                    it.filter { movieModelWithGenres ->
                        movieModelWithGenres.mediaCategoryMapping.contains(
                            MediaIdMediaCategoryMapping(
                                movieId = movieModelWithGenres.mediaModel.id,
                                category = mediaCategory
                            )
                        )
                    }
                }
                .distinctUntilChanged()
                .map { Result.Success(it) },
            flow {
                emit(fetchTrendingMoviesFromNetwork(mediaCategory))
            }.filter {
                it !is NetworkResponseWrapper.Success
            }.map {
                Result.Error(it)
            }
        )
    }

    override fun flowTvMediaFromDb(
        tvMediaCategory: TVMEDIACATEGORY,
        page: Int
    ): Flow<Result<List<TvModelWithGenres>, NetworkResponseWrapper<TvMediaResponseContainer>>> {
        return merge(
            tvDao.fetchAllTvMediaWithGenre().distinctUntilChanged().map { Result.Success(it) },
            flow {
                emit(fetchTrendingTvFromNetwork(tvMediaCategory, page))

            }.filter { it !is NetworkResponseWrapper.Success }.map {
                Result.Error(it)
            }
        )
    }

    override suspend fun fetchTrendingMoviesFromNetwork(
        mediaCategory: MEDIACATEGORY,
        page: Int
    ): NetworkResponseWrapper<MediaResponseContainer> {
        return feedApiMapper.feedMovieMediaApiMapper.getOrDefault(
            mediaCategory
        ) { trendingMoviesNetworkDataSource.fetchTrendingMovies(it) }.invoke(page).also {
            if (it is NetworkResponseWrapper.Success) {
                movieDao.saveAllTrendingMovies(it.body.movieList.map { mediaModel ->
                    mediaModel.copy(
                        page = page
                    )
                })
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

    private suspend fun fetchTrendingTvFromNetwork(
        mediaCategory: TVMEDIACATEGORY,
        page: Int
    ): NetworkResponseWrapper<TvMediaResponseContainer> {
        return feedApiMapper.feedTvMediaApiMapper.getOrDefault(
            mediaCategory
        ) { trendingMoviesNetworkDataSource.fetchTrendingTv(it) }.invoke(page).also {
            if (it is NetworkResponseWrapper.Success) {
                tvDao.saveAllTrendingTvMedia(it.body.tvModelList.map { mediaModel ->
                    mediaModel.copy(
                        page = page
                    )
                })
                tvMediaCategoryDao.saveGenreIdsFromTvMedia(it.body.tvModelList.map { mediaModel ->
                    TvMediaIdMediaCategoryMapping(
                        mediaModel.id,
                        mediaCategory
                    )
                })
                it.body.tvModelList.forEach { movieModel ->
                    tvIdGenreIdMappingDao.saveGenreIdsFromTvMedia(
                        movieModel
                    )
                }
            }
        }
    }


}