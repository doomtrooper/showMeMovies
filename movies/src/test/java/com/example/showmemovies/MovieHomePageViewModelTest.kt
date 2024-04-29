package com.example.showmemovies

import com.example.showmemovies.homefeed.MovieHomePageViewModel
import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.models.MediaIdMediaCategoryMapping
import com.example.showmemovies.models.MediaModel
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.models.MovieIdGenreIdMapping
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.repository.IGenreRepository
import com.example.showmemovies.repository.IHomeFeedsRepository
import com.example.showmemovies.repository.ITvGenreRepository
import com.example.showmemovies.utils.ErrorBody
import com.example.showmemovies.utils.NetworkResponseWrapper.ServiceError
import com.example.showmemovies.utils.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private val movieModel: MediaModel = MediaModel(
        false,
        "/44immBwzhDVyjn87b3x3l9mlhAD.jpg",
        934433,
        "Scream VI",
        "en",
        "Scream VI",
        "Following the latest Ghostface killings, the four survivors leave Woodsboro behind and start a fresh chapter.",
        "/wDWwtvkRRlgTiUr6TyLSMX8FCuZ.jpg",
        609.941,
        "2023-03-08",
        false,
        7.374,
        684
    )
    private val movieModel2 = movieModel.copy(id = 934434, title = "Scream V")

    private val movieIdGenreIdMappings = listOf(MovieIdGenreIdMapping(movieId = movieModel.id, 1L))
    private val mediaCategoryMapping =
        listOf(MediaIdMediaCategoryMapping(movieId = movieModel.id, MEDIACATEGORY.TRENDING_MOVIE))
    private val mediaCategoryMapping2 =
        listOf(MediaIdMediaCategoryMapping(movieId = movieModel.id, MEDIACATEGORY.UPCOMING_MOVIE))
    private val movieModelWithGenres =
        MovieModelWithGenres(movieModel, movieIdGenreIdMappings, mediaCategoryMapping)

    private val movieModelWithGenres2 =
        MovieModelWithGenres(movieModel, movieIdGenreIdMappings, mediaCategoryMapping2)


    private val data =
        MediaResponseContainer(
            totalPages = 0,
            totalResults = 0,
            page = 0,
            movieList = listOf(movieModel)
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
    lateinit var repository: IHomeFeedsRepository
    @MockK
    lateinit var genreRepository: IGenreRepository
    @MockK
    lateinit var tvGenreRepository: ITvGenreRepository
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
                emit(Result.Success(listOf(movieModelWithGenres, movieModelWithGenres2)))
            }
            coEvery { repository.flowTvMediaFromDb() } returns emptyFlow()
            coEvery { tvGenreRepository.flowTvGenresFromDb() } returns emptyFlow()
            coEvery { genreRepository.flowGenresFromDb() } returns emptyFlow()

            val homePageViewModel =
                MovieHomePageViewModel(
                    repository,
                    tvGenreRepository,
                    genreRepository,
                    unConfinedTestDispatcher
                )
            advanceUntilIdle()
            coVerify {
                repository.flowTrendingMoviesFromDb()
            }
            assert(homePageViewModel.uiState.value.trendingMovies.isNotEmpty())
            assert(homePageViewModel.uiState.value.upcomingMovies.isNotEmpty())
            assert(homePageViewModel.uiState.value.trendingMovies == listOf(movieModelWithGenres))
            assert(homePageViewModel.uiState.value.upcomingMovies == listOf(movieModelWithGenres2))
        }
    }

    @Test
    fun `should contain stale data when network call is failure and db has stale data`() {
        runTest(unConfinedTestDispatcher) {
            coEvery { repository.flowTrendingMoviesFromDb() } returns flow {
                emit(Result.Success(listOf(movieModelWithGenres, movieModelWithGenres2)))
                emit(Result.Error(body = ServiceError(errorBody)))
            }
            coEvery { repository.flowTvMediaFromDb() } returns emptyFlow()
            coEvery { tvGenreRepository.flowTvGenresFromDb() } returns emptyFlow()
            coEvery { genreRepository.flowGenresFromDb() } returns emptyFlow()

            val homePageViewModel =
                MovieHomePageViewModel(
                    repository,
                    tvGenreRepository,
                    genreRepository,
                    unConfinedTestDispatcher
                )
            advanceUntilIdle()
            coVerify {
                repository.flowTrendingMoviesFromDb()
            }
            assert(homePageViewModel.uiState.value.trendingMovies.isNotEmpty())
            assert(homePageViewModel.uiState.value.upcomingMovies.isNotEmpty())
            assert(homePageViewModel.uiState.value.trendingMovies == listOf(movieModelWithGenres))
            assert(homePageViewModel.uiState.value.upcomingMovies == listOf(movieModelWithGenres2))
            assert(homePageViewModel.uiState.value.error)
            assert(errorBody == homePageViewModel.uiState.value.errorWrapper?.serviceErrorBody)
        }
    }

}