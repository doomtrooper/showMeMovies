package com.example.showmemovies.repository

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.datasource.dao.TvGenreMappingDao
import com.example.showmemovies.datasource.network.ITvGenreNetworkDataSource
import com.example.showmemovies.models.TvGenreNameIdMapping
import com.example.showmemovies.models.TvGenreNameIdMappingContainer
import com.example.showmemovies.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

interface ITvGenreRepository {
    suspend fun flowTvGenresFromDb(): Flow<Result<List<TvGenreNameIdMapping>, NetworkResponseWrapper<TvGenreNameIdMappingContainer>>>
}

class TvGenreRepository @Inject constructor(
    private val tvGenreNetworkDataSource: ITvGenreNetworkDataSource,
    private val tvGenreMappingDao: TvGenreMappingDao
) : ITvGenreRepository {
    override suspend fun flowTvGenresFromDb(): Flow<Result<List<TvGenreNameIdMapping>, NetworkResponseWrapper<TvGenreNameIdMappingContainer>>> {
        return merge(
            tvGenreMappingDao.getAllGenre().map { Result.Success(it) },
            flow {
                emit(fetchTvGenreFromNetwork())
            }.filter { it !is NetworkResponseWrapper.Success }.map { Result.Error(it) }
        )
    }

    private suspend fun fetchTvGenreFromNetwork(): NetworkResponseWrapper<TvGenreNameIdMappingContainer> {
        return tvGenreNetworkDataSource.fetchTvMovieGenres().also {
            if (it is NetworkResponseWrapper.Success) {
                tvGenreMappingDao.saveGenre(it.body.genres)
            }
        }
    }


}