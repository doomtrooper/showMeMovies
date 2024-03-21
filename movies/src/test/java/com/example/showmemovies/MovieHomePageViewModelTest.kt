package com.example.showmemovies

import com.example.showmemovies.NetworkResponseWrapper.ServiceError
import com.example.showmemovies.NetworkResponseWrapper.Success
import com.example.showmemovies.models.MovieModel
import com.example.showmemovies.models.TrendingMoviesResponse
import com.example.showmemovies.repository.ITrendingMoviesRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class MovieHomePageViewModelTest {

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
    private val movieModel2 = movieModel.copy(id = 934434, title = "Scream V")

    private val data =
        TrendingMoviesResponse(
            totalPages = 0,
            totalResults = 0,
            page = 0,
            movieList = listOf(movieModel)
        )

    private val data2 =
        TrendingMoviesResponse(
            totalPages = 0,
            totalResults = 0,
            page = 0,
            movieList = listOf(movieModel2)
        )


    private val errorBody = ErrorBody(statusCode = 500, "API failed", false)
    @MockK
    lateinit var repository: ITrendingMoviesRepository
    private val unConfinedTestDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should contain movies when network call is success and no db data`() = runTest {
        coEvery { repository.fetchTrendingMovies() } returns flow { emit(Success(data)) }
        val homePageViewModel = MovieHomePageViewModel(repository, unConfinedTestDispatcher)
        advanceUntilIdle()
        coVerify {
            repository.fetchTrendingMovies()
        }
        assert(homePageViewModel.uiState.value.trendingMovies.isNotEmpty())
        assert(homePageViewModel.uiState.value.trendingMovies[0] == movieModel)
    }

    @Test
    fun `should contain error when network call is failure and no db data`() = runTest {
        coEvery { repository.fetchTrendingMovies() } returns flow { emit(ServiceError(errorBody)) }
        val homePageViewModel = MovieHomePageViewModel(repository, unConfinedTestDispatcher)
        advanceUntilIdle()
        coVerify {
            repository.fetchTrendingMovies()
        }
        assert(!homePageViewModel.uiState.value.loading)
        assert(homePageViewModel.uiState.value.error)
        assert(
            homePageViewModel.uiState.value.errorWrapper?.serviceErrorBody?.equals(errorBody)
                ?: false
        )
    }

    @Test
    fun `should contain same movie when network call is success and db also have same stale data`() = runTest {
        coEvery { repository.fetchTrendingMovies() } returns flow {
            emit(Success(data))
            delay(1000)
            emit(Success(data))
        }
        val homePageViewModel = MovieHomePageViewModel(repository, StandardTestDispatcher())
        advanceTimeBy(100)
        coVerify {
            repository.fetchTrendingMovies()
        }
        assert(homePageViewModel.uiState.value.trendingMovies.isNotEmpty())
        assert(homePageViewModel.uiState.value.trendingMovies[0] == movieModel)
        advanceUntilIdle()
        assert(homePageViewModel.uiState.value.trendingMovies[0] == movieModel)
    }

    @Test
    fun `should update when network call is success and db also have stale data`() = runTest {
        coEvery { repository.fetchTrendingMovies() } returns flow {
            emit(Success(data))
            delay(1000)
            emit(Success(data2))
        }
        val homePageViewModel = MovieHomePageViewModel(repository, StandardTestDispatcher())
        advanceTimeBy(100)
        coVerify {
            repository.fetchTrendingMovies()
        }
        assert(homePageViewModel.uiState.value.trendingMovies.isNotEmpty())
        assert(homePageViewModel.uiState.value.trendingMovies[0] == movieModel)
        advanceUntilIdle()
        assert(homePageViewModel.uiState.value.trendingMovies[0] == movieModel2)
    }

    @Test
    fun `should contain movie and error when network call is failure and db have stale data`() = runTest {
        coEvery { repository.fetchTrendingMovies() } returns flow {
            emit(Success(data))
            delay(1000)
            emit(ServiceError(errorBody))
        }
        val homePageViewModel = MovieHomePageViewModel(repository, StandardTestDispatcher())
        advanceTimeBy(100)
        coVerify {
            repository.fetchTrendingMovies()
        }
        assert(homePageViewModel.uiState.value.trendingMovies.isNotEmpty())
        assert(homePageViewModel.uiState.value.trendingMovies[0] == movieModel)
        advanceUntilIdle()
        assert(
            homePageViewModel.uiState.value.errorWrapper?.serviceErrorBody?.equals(errorBody)
                ?: false
        )
    }

}