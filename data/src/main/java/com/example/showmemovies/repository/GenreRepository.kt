package com.example.showmemovies.repository

import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.datasource.dao.GenreMappingDao
import com.example.showmemovies.datasource.network.IGenreNetworkDataSource
import com.example.showmemovies.models.GenreNameIdMapping
import com.example.showmemovies.models.GenreNameIdMappingContainer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface IGenreRepository {
    suspend fun flowGenresFromDb(): Flow<List<GenreNameIdMapping>>
    suspend fun fetchGenreFromNetwork(): NetworkResponseWrapper<GenreNameIdMappingContainer>
}

class GenreRepository @Inject constructor(
    private val genreNetworkDataSource: IGenreNetworkDataSource,
    private val genreMappingDao: GenreMappingDao
) : IGenreRepository {
    override suspend fun flowGenresFromDb(): Flow<List<GenreNameIdMapping>> {
        return genreMappingDao.getAllGenre()
    }

    override suspend fun fetchGenreFromNetwork(): NetworkResponseWrapper<GenreNameIdMappingContainer> {
        return genreNetworkDataSource.fetchMovieGenres().also {
            if (it is NetworkResponseWrapper.Success) {
                genreMappingDao.saveGenre(it.body.genres)
            }
        }
    }


}