package com.example.showmemovies.repository

import app.cash.turbine.test
import com.example.showmemovies.utils.ErrorBody
import com.example.showmemovies.MainCoroutineRule
import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.datasource.network.ITendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.dao.TrendingMovieDao
import com.example.showmemovies.models.MovieModel
import com.example.showmemovies.models.TrendingMoviesResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrendingMoviesRepositoryTest {
    @MockK
    private lateinit var trendingMovieDao: TrendingMovieDao

    @MockK
    private lateinit var trendingNetworkDataSource: ITendingMoviesNetworkDataSource

    private lateinit var repository: ITrendingMoviesRepository

    private val movieModel: MovieModel = MovieModel(
        false,
        "/44immBwzhDVyjn87b3x3l9mlhAD.jpg",
        934433,
        "Scream VI",
        "en",
        "Scream VI",
        "Following the latest Ghostface killings, the four survivors leave Woodsboro behind and start a fresh chapter.",
        "/wDWwtvkRRlgTiUr6TyLSMX8FCuZ.jpg",
        "movie",
        609.941,
        "2023-03-08",
        false,
        7.374,
        684
    )
    private val data =
        TrendingMoviesResponse(
            totalPages = 0,
            totalResults = 0,
            page = 0,
            movieList = listOf(movieModel)
        )

    private val errorBody = ErrorBody(statusCode = 500, "API failed", false)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = TrendingMoviesRepository(trendingNetworkDataSource, trendingMovieDao)
    }

    @Test
    fun `fetch trending movies no db just network`() = runTest {
        val success = NetworkResponseWrapper.Success(
            data
        )

        coEvery { trendingNetworkDataSource.fetchTrendingMovies() } returns flow { emit(success) }
        coEvery { trendingMovieDao.getAllTrendingMovies() } returns emptyFlow()

        repository.flowTrendingMoviesFromDb().test {
            val networkResponseWrapper = awaitItem()
            cancelAndConsumeRemainingEvents()
            assert(networkResponseWrapper == success)
            coVerify { trendingNetworkDataSource.fetchTrendingMovies() }
        }
    }

    @Test
    fun `fetch trending movies stale db and network`() = runTest {
        val success = NetworkResponseWrapper.Success(
            data
        )

        coEvery { trendingNetworkDataSource.fetchTrendingMovies() } returns flow {
            delay(100)
            emit(success)
        }
        coEvery { trendingMovieDao.getAllTrendingMovies() } returns flow {
            emit(listOf(movieModel))
        }

        repository.flowTrendingMoviesFromDb().test {
            val dbData = awaitItem()
            val networkData = awaitItem()
            cancelAndConsumeRemainingEvents()
            assert(dbData is NetworkResponseWrapper.Success)
            assert(networkData is NetworkResponseWrapper.Success)
            val responseSuccess = dbData as NetworkResponseWrapper.Success
            assert(responseSuccess.body.movieList[0] == movieModel)
            assert(networkData == success)
            coVerify { trendingNetworkDataSource.fetchTrendingMovies() }
            coVerify { trendingMovieDao.getAllTrendingMovies() }
        }
    }

    @Test
    fun `fetch trending movies stale db and network fails`() = runTest {
        val serviceError = NetworkResponseWrapper.ServiceError(
            errorBody
        )

        coEvery { trendingNetworkDataSource.fetchTrendingMovies() } returns flow {
            delay(100)
            emit(serviceError)
        }
        coEvery { trendingMovieDao.getAllTrendingMovies() } returns flow {
            emit(listOf(movieModel))
        }

        repository.flowTrendingMoviesFromDb().test {
            val dbData = awaitItem()
            val networkData = awaitItem()
            cancelAndConsumeRemainingEvents()
            assert(dbData is NetworkResponseWrapper.Success)
            assert(networkData is NetworkResponseWrapper.ServiceError)
            val responseSuccess = dbData as NetworkResponseWrapper.Success
            assert(responseSuccess.body.movieList[0] == movieModel)
            assert(networkData == serviceError)
            coVerify { trendingNetworkDataSource.fetchTrendingMovies() }
            coVerify { trendingMovieDao.getAllTrendingMovies() }
        }
    }

}