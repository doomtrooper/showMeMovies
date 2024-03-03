package com.example.showmemovies

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
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class MovieHomePageViewModelTest {

    private val data = TrendingMoviesResponse(totalPages = 0, totalResults = 0, page = 0L, movieList = listOf())
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
    @MockK
    lateinit var repository: ITrendingMoviesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        MockKAnnotations.init(this)
    }

    @Test
    fun `should return non null list when api fetch trending movies is success`() {
        coEvery { repository.fetchTrendingMovies() } returns Success(data)
        val homePageViewModel = MovieHomePageViewModel(repository)
        coVerify {
            repository.fetchTrendingMovies()
        }
        assert(!homePageViewModel.uiState.value.loading)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}