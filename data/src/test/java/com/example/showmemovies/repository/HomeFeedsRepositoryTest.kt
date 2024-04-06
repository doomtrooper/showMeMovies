package com.example.showmemovies.repository

import app.cash.turbine.test
import com.example.showmemovies.utils.ErrorBody
import com.example.showmemovies.MainCoroutineRule
import com.example.showmemovies.datasource.dao.MediaCategoryDao
import com.example.showmemovies.datasource.dao.MovieIdGenreIdMappingDao
import com.example.showmemovies.datasource.network.ITendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.dao.MovieDao
import com.example.showmemovies.datasource.dao.TvDao
import com.example.showmemovies.datasource.dao.TvIdGenreIdMappingDao
import com.example.showmemovies.datasource.dao.TvMediaCategoryDao
import com.example.showmemovies.models.MEDIACATEGORY.*
import com.example.showmemovies.models.MediaIdMediaCategoryMapping
import com.example.showmemovies.models.MovieIdGenreIdMapping
import com.example.showmemovies.models.MediaModel
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.utils.FeedApiMapper
import com.example.showmemovies.utils.NetworkResponseWrapper.*
import com.example.showmemovies.utils.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeFeedsRepositoryTest {
    @MockK
    private lateinit var trendingNetworkDataSource: ITendingMoviesNetworkDataSource

    @MockK
    private lateinit var movieDao: MovieDao

    @MockK
    private lateinit var tvDao: TvDao

    @MockK
    private lateinit var mediaCategoryDao: MediaCategoryDao

    @MockK
    private lateinit var tvMediaCategoryDao: TvMediaCategoryDao

    @MockK
    private lateinit var movieIdGenreIdMappingDao: MovieIdGenreIdMappingDao

    @MockK
    private lateinit var tvIdGenreIdMappingDao: TvIdGenreIdMappingDao

    private val feedApiMapper =
        FeedApiMapper(feedMovieMediaApiMapper = emptyMap(), feedTvMediaApiMapper = emptyMap())

    private lateinit var repository: IHomeFeedsRepository

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
    private val mediaResponseContainer =
        MediaResponseContainer(
            totalPages = 0,
            totalResults = 0,
            page = 0,
            movieList = listOf(movieModel)
        )
    private val movieIdGenreIdMappings = listOf(MovieIdGenreIdMapping(movieId = movieModel.id, 1L))
    private val mediaCategoryMapping =
        listOf(MediaIdMediaCategoryMapping(movieId = movieModel.id, TRENDING_MOVIE))
    private val movieModelWithGenres =
        MovieModelWithGenres(movieModel, movieIdGenreIdMappings, mediaCategoryMapping)

    private val errorBody = ErrorBody(statusCode = 500, "API failed", false)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = HomeFeedsRepository(
            trendingMoviesNetworkDataSource = trendingNetworkDataSource,
            movieDao = movieDao,
            tvDao = tvDao,
            mediaCategoryDao = mediaCategoryDao,
            tvMediaCategoryDao = tvMediaCategoryDao,
            movieIdGenreIdMappingDao = movieIdGenreIdMappingDao,
            tvIdGenreIdMappingDao = tvIdGenreIdMappingDao,
            feedApiMapper = feedApiMapper
        )
    }

    @Test
    fun `observe trending movies from db when db is empty and network call is success`() = runTest {
        coEvery { movieDao.fetchAllMoviesWithGenre() } returns flow {
            emit(listOf())
            emit(listOf(movieModelWithGenres))
        }
        coEvery { trendingNetworkDataSource.fetchTrendingMovies() } returns Success(
            mediaResponseContainer
        )
        coJustRun { movieDao.saveAllTrendingMovies(listOf(movieModel)) }
        coJustRun { mediaCategoryDao.saveGenreIdsFromMovie((mediaCategoryMapping)) }
        coJustRun { movieIdGenreIdMappingDao.saveGenreIdsFromMovie((movieModel)) }
        repository.flowTrendingMoviesFromDb().test {
            val data1 = awaitItem()
            val data2 = awaitItem()
            cancelAndConsumeRemainingEvents()
            coVerify { movieDao.saveAllTrendingMovies(listOf(movieModel)) }
            coVerify { mediaCategoryDao.saveGenreIdsFromMovie((mediaCategoryMapping)) }
            coVerify { movieIdGenreIdMappingDao.saveGenreIdsFromMovie((movieModel)) }
            assert(data1 is Result.Success)
            if (data1 is Result.Success) {
                assert(data1.body.isEmpty())
            }

            assert(data2 is Result.Success)
            if (data2 is Result.Success) {
                assert(data2.body == listOf(movieModelWithGenres))
            }
        }
    }

    @Test
    fun `observe trending movies from db when db is empty and network call is failure`() = runTest {
        coEvery { movieDao.fetchAllMoviesWithGenre() } returns flow {
            emit(listOf())
        }
        coEvery { trendingNetworkDataSource.fetchTrendingMovies() } returns ServiceError(errorBody)
        repository.flowTrendingMoviesFromDb().test {
            val data1 = awaitItem()
            cancelAndConsumeRemainingEvents()
            coVerify(inverse = true) { movieDao.saveAllTrendingMovies(listOf(movieModel)) }
            coVerify(inverse = true) { mediaCategoryDao.saveGenreIdsFromMovie((mediaCategoryMapping)) }
            coVerify(inverse = true) { movieIdGenreIdMappingDao.saveGenreIdsFromMovie((movieModel)) }
            assert(data1 is Result.Success)
            if (data1 is Result.Success) {
                assert(data1.body.isEmpty())
            }
        }
    }


    @Test
    fun `observe trending movies from db when db is non-empty and network call is failure`() =
        runTest {
            coEvery { movieDao.fetchAllMoviesWithGenre() } returns flow {
                emit(listOf(movieModelWithGenres))
            }
            coEvery { trendingNetworkDataSource.fetchTrendingMovies() } returns ServiceError(
                errorBody
            )
            repository.flowTrendingMoviesFromDb().test {
                val data1 = awaitItem()
                cancelAndConsumeRemainingEvents()
                coVerify(inverse = true) { movieDao.saveAllTrendingMovies(listOf(movieModel)) }
                coVerify(inverse = true) { mediaCategoryDao.saveGenreIdsFromMovie((mediaCategoryMapping)) }
                coVerify(inverse = true) { movieIdGenreIdMappingDao.saveGenreIdsFromMovie((movieModel)) }
                assert(data1 is Result.Success)
                if (data1 is Result.Success) {
                    assert(data1.body == listOf(movieModelWithGenres))
                }
            }
        }

}