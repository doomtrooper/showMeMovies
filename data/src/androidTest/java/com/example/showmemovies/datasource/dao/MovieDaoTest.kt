package com.example.showmemovies.datasource.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.example.showmemovies.AppDatabase
import com.example.showmemovies.MainCoroutineRule
import com.example.showmemovies.models.MEDIACATEGORY.*
import com.example.showmemovies.models.MediaIdMediaCategoryMapping
import com.example.showmemovies.models.MediaModel
import com.example.showmemovies.models.MovieIdGenreIdMapping
import com.example.showmemovies.models.MovieModelWithGenres
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var movieDao: MovieDao
    private lateinit var movieIdGenreIdMappingDao: MovieIdGenreIdMappingDao
    private lateinit var mediaCategoryDao: MediaCategoryDao
    private val unConfinedTestDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule(unConfinedTestDispatcher)
    private val movieIdGenreIdMapping = MovieIdGenreIdMapping(movieId = 934433, genreId = 1L)
    private val movieModel: MediaModel = MediaModel(
        adult = false,
        backdropPath = "/44immBwzhDVyjn87b3x3l9mlhAD.jpg",
        id = 934433,
        title = "Scream VI",
        originalLanguage = "en",
        originalTitle = "Scream VI",
        overview = "Following the latest Ghostface killings, the four survivors leave Woodsboro behind and start a fresh chapter.",
        posterPath = "/wDWwtvkRRlgTiUr6TyLSMX8FCuZ.jpg",
        popularity = 609.941,
        releaseDate = "2023-03-08",
        video = false,
        voteAverage = 7.374,
        voteCount = 684,
    ).also {
        it.genreIds = listOf(movieIdGenreIdMapping.genreId)
    }
    private val mediaIdCategoryMapping = MediaIdMediaCategoryMapping(movieId = 934433L, category = TRENDING_MOVIE)

    private val movieModel2 = movieModel.copy(id = 934432)

    @Before
    fun createDb() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        movieDao = db.moviesDao()
        movieIdGenreIdMappingDao  = db.movieIdGenreIdMappingDao()
        mediaCategoryDao = db.mediaCategoryDao()
    }

    @Test
    @Throws(Exception::class)
    fun emitEmptyListWhenDbEmpty() = runBlocking {
        movieDao.fetchAllMoviesWithGenre().test {
            val result = awaitItem()
            assert(result == emptyList<MovieModelWithGenres>())
        }
    }

    @Test
    @Throws(Exception::class)
    fun emitEmptyListWhenDataIsClearedFromDb() = runBlocking {
        movieDao.saveAllTrendingMovies(listOf(movieModel))
        movieDao.getAllMovies().test {
            val result: List<MediaModel> = awaitItem()
            assert(result.isNotEmpty())
            movieDao.deleteAll()
            val result2: List<MediaModel> = awaitItem()
            assert(result2.isEmpty())
        }
    }

    @Test
    @Throws(Exception::class)
    fun emitDataWithRelations() = runBlocking {
        movieDao.saveAllTrendingMovies(listOf(movieModel))
        mediaCategoryDao.saveGenreIdsFromMovie(listOf(mediaIdCategoryMapping))
        movieIdGenreIdMappingDao.saveGenreIdsFromMovie(movieModel)
        movieDao.fetchAllMoviesWithGenre().test {
            val result: List<MovieModelWithGenres> = awaitItem()
            assert(result.isNotEmpty())
            assert(result[0].mediaModel == movieModel)
            assert(result[0].genreIdMapping == listOf(movieIdGenreIdMapping))
            assert(result[0].mediaCategoryMapping == listOf(mediaIdCategoryMapping))
        }
    }

    @Test
    @Throws(Exception::class)
    fun emitDataWithRelationsWhenRelationsDataIsUpdated() = runBlocking {
        movieDao.saveAllTrendingMovies(listOf(movieModel))
        movieDao.fetchAllMoviesWithGenre().test {
            val result: List<MovieModelWithGenres> = awaitItem()
            assert(result.isNotEmpty())
            assert(result[0].mediaModel == movieModel)
            assert(result[0].genreIdMapping.isEmpty())
            assert(result[0].mediaCategoryMapping.isEmpty())
            mediaCategoryDao.saveGenreIdsFromMovie(listOf(mediaIdCategoryMapping))
            movieIdGenreIdMappingDao.saveGenreIdsFromMovie(movieModel)
            val result2: List<MovieModelWithGenres> = awaitItem()
            assert(result2[0].genreIdMapping == listOf(movieIdGenreIdMapping))
            assert(result2[0].mediaCategoryMapping == listOf(mediaIdCategoryMapping))
        }
    }

    @Test
    @Throws(Exception::class)
    fun emitDataWhenTableUpdated() = runBlocking {
        movieDao.saveAllTrendingMovies(listOf(movieModel))
        mediaCategoryDao.saveGenreIdsFromMovie(listOf(mediaIdCategoryMapping))
        movieIdGenreIdMappingDao.saveGenreIdsFromMovie(movieModel)
        movieDao.fetchAllMoviesWithGenre().test {
            val result: List<MovieModelWithGenres> = awaitItem()
            assert(result.isNotEmpty())
            assert(result[0].mediaModel == movieModel)
            assert(result[0].genreIdMapping == listOf(movieIdGenreIdMapping))
            assert(result[0].mediaCategoryMapping == listOf(mediaIdCategoryMapping))
            movieDao.saveAllTrendingMovies(listOf(movieModel2))
            val result2: List<MovieModelWithGenres> = awaitItem()
            assert(result2.isNotEmpty())
            assert(result2[0].mediaModel == movieModel2)
            assert(result2[0].genreIdMapping.isEmpty())
            assert(result2[0].mediaCategoryMapping.isEmpty())
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

}