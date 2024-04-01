package com.example.showmemovies.repository

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.datasource.dao.TvGenreMappingDao
import com.example.showmemovies.datasource.network.ITvGenreNetworkDataSource
import com.example.showmemovies.models.TvGenreNameIdMapping
import com.example.showmemovies.models.TvGenreNameIdMappingContainer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ITvGenreRepository {
    suspend fun flowTvGenresFromDb(): Flow<List<TvGenreNameIdMapping>>
    suspend fun fetchTvGenreFromNetwork(): NetworkResponseWrapper<TvGenreNameIdMappingContainer>
}

class TvGenreRepository @Inject constructor(
    private val tvGenreNetworkDataSource: ITvGenreNetworkDataSource,
    private val tvGenreMappingDao: TvGenreMappingDao
) : ITvGenreRepository {
    override suspend fun flowTvGenresFromDb(): Flow<List<TvGenreNameIdMapping>> {
        return tvGenreMappingDao.getAllGenre()
    }

    override suspend fun fetchTvGenreFromNetwork(): NetworkResponseWrapper<TvGenreNameIdMappingContainer> {
        return tvGenreNetworkDataSource.fetchTvMovieGenres().also {
            if (it is NetworkResponseWrapper.Success) {
                tvGenreMappingDao.saveGenre(it.body.genres)
            }
        }
    }


}