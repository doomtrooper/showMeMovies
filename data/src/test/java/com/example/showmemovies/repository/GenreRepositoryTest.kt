package com.example.showmemovies.repository

import app.cash.turbine.test
import com.example.showmemovies.MainCoroutineRule
import com.example.showmemovies.datasource.dao.GenreMappingDao
import com.example.showmemovies.datasource.network.IGenreNetworkDataSource
import com.example.showmemovies.models.GenreNameIdMapping
import com.example.showmemovies.models.GenreNameIdMappingContainer
import com.example.showmemovies.utils.ErrorBody
import com.example.showmemovies.utils.NetworkResponseWrapper.*
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        genreRepository = GenreRepository(genreNetworkDataSource, genreMappingDao)
    }

    @Test
    fun `observe genre when db is empty`() = runTest {
        coEvery { genreMappingDao.getAllGenre() } returns flow { emit(listOf()) }
        genreRepository.flowGenresFromDb().test {
            val genreNameIdMappings = awaitItem()
            cancelAndConsumeRemainingEvents()
            coVerify { genreMappingDao.getAllGenre() }
            assert(genreNameIdMappings.isEmpty())
        }
    }

    @Test
    fun `observe genre table when db is non-empty`() = runTest {
        coEvery { genreMappingDao.getAllGenre() } returns flow { emit(listOf(genreNameIdMapping)) }
        genreRepository.flowGenresFromDb().test {
            val result = awaitItem()
            cancelAndConsumeRemainingEvents()
            coVerify { genreMappingDao.getAllGenre() }
            assert(result.isNotEmpty())
            assert(result == listOf(genreNameIdMapping))
        }
    }

    @Test
    fun `genre list saved to db when api response is success`() = runTest {
        coEvery { genreNetworkDataSource.fetchMovieGenres() } returns Success(
            GenreNameIdMappingContainer((listOf(genreNameIdMapping)))
        )

        coEvery { genreMappingDao.saveGenre(listOf(genreNameIdMapping)) } returns Unit
        val responseWrapper = genreRepository.fetchGenreFromNetwork()
        coVerify { genreNetworkDataSource.fetchMovieGenres() }
        coVerify { genreMappingDao.saveGenre(listOf(genreNameIdMapping)) }

        assert(responseWrapper is Success)
        if (responseWrapper is Success) {
            assert(responseWrapper.body == GenreNameIdMappingContainer((listOf(genreNameIdMapping))))
        }
    }

    @Test
    fun `genre list saved to db when api response is failure`() = runTest {
        coEvery { genreNetworkDataSource.fetchMovieGenres() } returns ServiceError(
            errorBody
        )

        coEvery { genreMappingDao.saveGenre(listOf(genreNameIdMapping)) } returns Unit
        val responseWrapper = genreRepository.fetchGenreFromNetwork()
        coVerify { genreNetworkDataSource.fetchMovieGenres() }
        coVerify(inverse = true) { genreMappingDao.saveGenre(listOf(genreNameIdMapping)) }

        assert(responseWrapper is ServiceError)
        if (responseWrapper is ServiceError) {
            assert(responseWrapper.errorBody == errorBody)
        }
    }

}