package com.example.showmemovies

import com.example.showmemovies.models.GenreNameIdMappingContainer
import com.example.showmemovies.models.MovieIdGenreIdMapping
import com.example.showmemovies.models.MediaModel
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.repository.IGenreRepository
import com.example.showmemovies.repository.ITrendingMoviesRepository
import com.example.showmemovies.utils.ErrorBody
import com.example.showmemovies.utils.NetworkResponseWrapper.ServiceError
import com.example.showmemovies.utils.NetworkResponseWrapper.Success
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class MovieHomePageViewModelTest {

    private val mediaModel: MediaModel = MediaModel(
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
    private val movieModel2 = mediaModel.copy(id = 934434, title = "Scream V")
    private val movieIdGenreIdMappings = listOf(MovieIdGenreIdMapping(movieId = mediaModel.id, 1L))
    private val movieIdGenreIdMappings2 =
        listOf(MovieIdGenreIdMapping(movieId = movieModel2.id, 1L))
    private val movieModelWithGenre: MovieModelWithGenres =
        MovieModelWithGenres(mediaModel, movieIdGenreIdMappings)
    private val movieModelWithGenre2: MovieModelWithGenres =
        MovieModelWithGenres(movieModel2, movieIdGenreIdMappings2)

    private val data =
        MediaResponseContainer(
            totalPages = 0,
            totalResults = 0,
            page = 0,
            movieList = listOf(mediaModel)
        )

    private val data2 =
        MediaResponseContainer(
            totalPages = 0,
            totalResults = 0,
            page = 0,
            movieList = listOf(movieModel2)
        )


    private val errorBody = ErrorBody(statusCode = 500, "API failed", false)

    @MockK
    lateinit var repository: ITrendingMoviesRepository

    @MockK
    lateinit var genreRepository: IGenreRepository
    private val unConfinedTestDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val standardTestDispatcher: TestDispatcher = StandardTestDispatcher()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule(unConfinedTestDispatcher)


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should contain stale data when network call is success with empty result and db has stale data`() {
        runTest(unConfinedTestDispatcher) {
            coEvery { repository.flowTrendingMoviesFromDb() } returns flow {
                emit(listOf(movieModelWithGenre))
            }
            coEvery { repository.fetchTrendingMoviesFromNetwork() } returns Success(
                MediaResponseContainer(movieList = listOf())
            )
            coEvery { genreRepository.flowGenresFromDb() } returns emptyFlow()
            coEvery { genreRepository.fetchGenreFromNetwork() } returns Success(
                GenreNameIdMappingContainer(genres = emptyList())
            )
            val homePageViewModel =
                MovieHomePageViewModel(repository, genreRepository, unConfinedTestDispatcher)
            advanceUntilIdle()
            coVerify {
                repository.flowTrendingMoviesFromDb()
            }
            assert(homePageViewModel.uiState.value.trendingMovies.isNotEmpty())
            assert(homePageViewModel.uiState.value.trendingMovies == listOf(movieModelWithGenre))
        }
    }


    @Test
    fun `should contain latest data when network call is success with non-empty result and db has stale data`() {
        runTest(unConfinedTestDispatcher) {
            coEvery { repository.flowTrendingMoviesFromDb() } returns flow {
                emit(listOf(movieModelWithGenre))
                delay(100)
                emit(listOf(movieModelWithGenre2))
            }
            coEvery { repository.fetchTrendingMoviesFromNetwork() } returns Success(
                MediaResponseContainer(movieList = listOf())
            )
            coEvery { genreRepository.flowGenresFromDb() } returns emptyFlow()
            coEvery { genreRepository.fetchGenreFromNetwork() } returns Success(
                GenreNameIdMappingContainer(genres = emptyList())
            )
            val homePageViewModel =
                MovieHomePageViewModel(repository, genreRepository, unConfinedTestDispatcher)
            advanceUntilIdle()
            coVerify {
                repository.flowTrendingMoviesFromDb()
            }
            assert(homePageViewModel.uiState.value.trendingMovies.isNotEmpty())
            assert(homePageViewModel.uiState.value.trendingMovies == listOf(movieModelWithGenre2))
        }
    }

    @Test
    fun `should contain latest data and error body when network call is failure and db has stale data`() {
        runTest(unConfinedTestDispatcher) {
            coEvery { repository.flowTrendingMoviesFromDb() } returns flow {
                emit(listOf(movieModelWithGenre))
            }
            coEvery { repository.fetchTrendingMoviesFromNetwork() } returns ServiceError(errorBody)
            coEvery { genreRepository.flowGenresFromDb() } returns emptyFlow()
            coEvery { genreRepository.fetchGenreFromNetwork() } returns Success(
                GenreNameIdMappingContainer(genres = emptyList())
            )
            val homePageViewModel =
                MovieHomePageViewModel(repository, genreRepository, unConfinedTestDispatcher)
            advanceUntilIdle()
            coVerify {
                repository.flowTrendingMoviesFromDb()
            }
            assert(homePageViewModel.uiState.value.trendingMovies.isNotEmpty())
            assert(homePageViewModel.uiState.value.trendingMovies == listOf(movieModelWithGenre))
            assert(homePageViewModel.uiState.value.error)
            assert(homePageViewModel.uiState.value.errorWrapper?.serviceErrorBody == errorBody)
        }
    }


}