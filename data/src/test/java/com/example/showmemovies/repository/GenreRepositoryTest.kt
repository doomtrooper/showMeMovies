package com.example.showmemovies.repository

import app.cash.turbine.test
import com.example.showmemovies.MainCoroutineRule
import com.example.showmemovies.datasource.dao.GenreMappingDao
import com.example.showmemovies.datasource.network.IGenreNetworkDataSource
import com.example.showmemovies.models.GenreNameIdMapping
import com.example.showmemovies.models.GenreNameIdMappingContainer
import com.example.showmemovies.utils.ErrorBody
import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.utils.NetworkResponseWrapper.*
import com.example.showmemovies.utils.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class GenreRepositoryTest {
    @MockK
    private lateinit var genreNetworkDataSource: IGenreNetworkDataSource

    @MockK
    private lateinit var genreMappingDao: GenreMappingDao

    private lateinit var genreRepository: IGenreRepository

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val genreNameIdMapping = GenreNameIdMapping("abcd", 123L)
    private val errorBody = ErrorBody(statusCode = 500, "API failed", false)
    private val genreResponse = GenreNameIdMappingContainer(genres = listOf( genreNameIdMapping))

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        genreRepository = GenreRepository(genreNetworkDataSource, genreMappingDao)
    }

    @Test
    fun `observe genre when db is empty and api response is success`() = runTest {
        coEvery { genreMappingDao.getAllGenre() } returns flow {
            emit(listOf())
            emit(listOf(genreNameIdMapping))
        }
        coEvery { genreNetworkDataSource.fetchMovieGenres() } returns Success(genreResponse)
        coEvery { genreMappingDao.saveGenre(genreResponse.genres) } returns Unit
        genreRepository.flowGenresFromDb().test {
            val item1: Result<List<GenreNameIdMapping>, NetworkResponseWrapper<GenreNameIdMappingContainer>> =
                awaitItem()
            val item2: Result<List<GenreNameIdMapping>, NetworkResponseWrapper<GenreNameIdMappingContainer>> =
                awaitItem()
            cancelAndConsumeRemainingEvents()
            coVerify { genreMappingDao.getAllGenre() }
            assert(item1 is Result.Success)
            if (item1 is Result.Success){
                assert(item1.body.isEmpty())
            }
            assert(item2 is Result.Success)
            if (item2 is Result.Success){
                assert(item2.body == genreResponse.genres)
            }
        }
    }

    @Test
    fun `observe genre table when db is non-empty and api response is success`() = runTest {
        coEvery { genreMappingDao.getAllGenre() } returns flow { emit(listOf(genreNameIdMapping)) }
        coEvery { genreNetworkDataSource.fetchMovieGenres() } returns Success(genreResponse)
        coEvery { genreMappingDao.saveGenre(genreResponse.genres) } returns Unit
        genreRepository.flowGenresFromDb().test {
            val item1: Result<List<GenreNameIdMapping>, NetworkResponseWrapper<GenreNameIdMappingContainer>> =
                awaitItem()
            cancelAndConsumeRemainingEvents()
            coVerify { genreMappingDao.getAllGenre() }
            assert(item1 is Result.Success)
            if (item1 is Result.Success){
                assert(item1.body == genreResponse.genres)
            }
        }
    }



    @Test
    fun `emit stale data error emit when api response is failure`() = runTest {
        coEvery { genreNetworkDataSource.fetchMovieGenres() } returns ServiceError(
            errorBody
        )
        coEvery { genreMappingDao.getAllGenre() } returns flow { emit(listOf(genreNameIdMapping)) }
        genreRepository.flowGenresFromDb().test {
            val item1: Result<List<GenreNameIdMapping>, NetworkResponseWrapper<GenreNameIdMappingContainer>> =
                awaitItem()
            val item2: Result<List<GenreNameIdMapping>, NetworkResponseWrapper<GenreNameIdMappingContainer>> =
                awaitItem()
            cancelAndConsumeRemainingEvents()
            coVerify { genreNetworkDataSource.fetchMovieGenres() }
            coVerify(inverse = true) { genreMappingDao.saveGenre(listOf(genreNameIdMapping)) }
            assert(item1 is Result.Success)
            if (item1 is Result.Success){
                assert(item1.body == genreResponse.genres)
            }
            assert(item2 is Result.Error)
            if (item2 is Result.Error && item2.body is ServiceError) {
                assert((item2.body as ServiceError).errorBody == errorBody)
            }
        }
    }

}