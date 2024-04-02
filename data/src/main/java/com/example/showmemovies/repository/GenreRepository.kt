package com.example.showmemovies.repository

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.datasource.dao.GenreMappingDao
import com.example.showmemovies.datasource.network.IGenreNetworkDataSource
import com.example.showmemovies.models.GenreNameIdMapping
import com.example.showmemovies.models.GenreNameIdMappingContainer
import com.example.showmemovies.utils.Result
import com.example.showmemovies.utils.Result.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

interface IGenreRepository {
    suspend fun flowGenresFromDb(): Flow<Result<List<GenreNameIdMapping>, NetworkResponseWrapper<GenreNameIdMappingContainer>>>
}

class GenreRepository @Inject constructor(
    private val genreNetworkDataSource: IGenreNetworkDataSource,
    private val genreMappingDao: GenreMappingDao
) : IGenreRepository {
    override suspend fun flowGenresFromDb(): Flow<Result<List<GenreNameIdMapping>, NetworkResponseWrapper<GenreNameIdMappingContainer>>> {
        return merge(
            genreMappingDao.getAllGenre().map { Success(it) },
            flow {
                emit(fetchGenreFromNetwork())
            }.filter { it !is NetworkResponseWrapper.Success }.map { Error(it) }
        )
    }

    private suspend fun fetchGenreFromNetwork(): NetworkResponseWrapper<GenreNameIdMappingContainer> {
        return genreNetworkDataSource.fetchMovieGenres().also {
            if (it is NetworkResponseWrapper.Success) {
                genreMappingDao.saveGenre(it.body.genres)
            }
        }
    }
}