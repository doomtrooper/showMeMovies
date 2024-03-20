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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
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
    private val data =
        TrendingMoviesResponse(
            totalPages = 0,
            totalResults = 0,
            page = 0,
            movieList = listOf(movieModel)
        )


    private val errorBody = ErrorBody(statusCode = 500, "API failed", false)
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @MockK
    lateinit var repository: ITrendingMoviesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        MockKAnnotations.init(this)
    }

    @Test
    fun `should return non null list when api fetch trending movies is success`() = runTest{
        coEvery { repository.fetchTrendingMovies() } returns flow { Success(data) }
        val homePageViewModel = MovieHomePageViewModel(repository)
        coVerify {
            repository.fetchTrendingMovies()
        }
        advanceUntilIdle()
        assert(homePageViewModel.uiState.value.trendingMovies[0] == movieModel)
    }

    @Test
    fun `should return error when api fetch trending movies is failure`() {
        coEvery { repository.fetchTrendingMovies() } returns flow { ServiceError(errorBody) }
        val homePageViewModel = MovieHomePageViewModel(repository)
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

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}