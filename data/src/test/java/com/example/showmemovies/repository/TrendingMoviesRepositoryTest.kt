package com.example.showmemovies.repository

import app.cash.turbine.test
import com.example.showmemovies.utils.ErrorBody
import com.example.showmemovies.MainCoroutineRule
import com.example.showmemovies.datasource.dao.MovieIdGenreIdMappingDao
import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.datasource.network.ITendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.dao.TrendingMovieDao
import com.example.showmemovies.models.MovieIdGenreIdMapping
import com.example.showmemovies.models.MovieModel
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.TrendingMoviesResponse
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

@OptIn(ExperimentalCoroutinesApi::class)
class TrendingMoviesRepositoryTest {
    @MockK
    private lateinit var trendingMovieDao: TrendingMovieDao

    @MockK
    private lateinit var movieIdGenreIdMappingDao: MovieIdGenreIdMappingDao

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
    private val movieIdGenreIdMappings = listOf(MovieIdGenreIdMapping(movieId = movieModel.id, 1L))
    private val errorBody = ErrorBody(statusCode = 500, "API failed", false)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = TrendingMoviesRepository(
            trendingNetworkDataSource,
            trendingMovieDao,
            movieIdGenreIdMappingDao
        )
    }

    @Test
    fun `observe trending movies from db when db is empty`() = runTest {
        coEvery { trendingMovieDao.fetchTrendingMoviesWithGenre() } returns flow {
            emit(listOf())
        }

        repository.flowTrendingMoviesFromDb().test {
            val emptyData = awaitItem()
            cancelAndConsumeRemainingEvents()
            coVerify { trendingMovieDao.fetchTrendingMoviesWithGenre() }
            assert(emptyData.isEmpty())
        }
    }


    @Test
    fun `observe trending movies from db when db has stale data`() = runTest {
        val modelWithGenres =
            MovieModelWithGenres(movieModel, genreIdMapping = movieIdGenreIdMappings)
        coEvery { trendingMovieDao.fetchTrendingMoviesWithGenre() } returns flow {
            emit(
                listOf(
                    modelWithGenres
                )
            )
        }

        repository.flowTrendingMoviesFromDb().test {
            val movieModelWithGenres = awaitItem()
            cancelAndConsumeRemainingEvents()
            coVerify { trendingMovieDao.fetchTrendingMoviesWithGenre() }
            assert(movieModelWithGenres.isNotEmpty())
            assert(movieModelWithGenres == listOf(modelWithGenres))
        }
    }

    @Test
    fun `fetch trending movies from network with success`() = runTest {
        val success = NetworkResponseWrapper.Success(data)

        coEvery { trendingNetworkDataSource.fetchTrendingMovies() } returns success
        coEvery { trendingMovieDao.updateNewTrendingMovies(data.movieList) } returns Unit
        coEvery { movieIdGenreIdMappingDao.saveGenreIdsFromMovie(movieModel) } returns Unit

        val fetchTrendingMoviesFromNetwork = repository.fetchTrendingMoviesFromNetwork()
        coVerify { trendingMovieDao.updateNewTrendingMovies(data.movieList) }
        coVerify { movieIdGenreIdMappingDao.saveGenreIdsFromMovie(movieModel) }
        assert(fetchTrendingMoviesFromNetwork is NetworkResponseWrapper.Success)
        if (fetchTrendingMoviesFromNetwork is NetworkResponseWrapper.Success) {
            assert(fetchTrendingMoviesFromNetwork.body == data)
        }
    }

    @Test
    fun `fetch trending movies from network with failure`() = runTest {
        val serviceError = NetworkResponseWrapper.ServiceError(
            errorBody
        )

        coEvery { trendingNetworkDataSource.fetchTrendingMovies() } returns serviceError

        val fetchTrendingMoviesFromNetwork = repository.fetchTrendingMoviesFromNetwork()
        assert(fetchTrendingMoviesFromNetwork is NetworkResponseWrapper.ServiceError)
        if (fetchTrendingMoviesFromNetwork is NetworkResponseWrapper.ServiceError) {
            assert(fetchTrendingMoviesFromNetwork.errorBody == errorBody)
        }
    }

}