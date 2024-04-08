package com.example.showmemovies.datasource.dao

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.example.showmemovies.AppDatabase
import com.example.showmemovies.MainCoroutineRule
import com.example.showmemovies.models.GenreNameIdMapping
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
@RunWith(AndroidJUnit4::class)
class GenreMappingDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var genreMappingDao: GenreMappingDao
    private val genreData = GenreNameIdMapping(genreId = 1L, genreName = "Action")
    private val genreData2 = GenreNameIdMapping(genreId = 2L, genreName = "Drama")
    private val genreData3 = GenreNameIdMapping(genreId = 1L, genreName = "Thriller")

    private val unConfinedTestDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule(unConfinedTestDispatcher)

    @Before
    fun createDb() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        genreMappingDao = db.genreDao()
    }

    @Test
    @Throws(Exception::class)
    fun emitEmptyListWhenDbEmpty() = runBlocking {
        genreMappingDao.getAllGenre().test {
            val result = awaitItem()
            assert(result == emptyList<GenreNameIdMapping>())
        }
    }

    @Test
    @Throws(Exception::class)
    fun emitItemWhenDbIsNotEmpty() = runBlocking {
        genreMappingDao.saveGenre(listOf(genreData))
        genreMappingDao.getAllGenre().test {
            val result = awaitItem()
            assert(result == listOf(genreData))
        }
    }

    @Test
    @Throws(Exception::class)
    fun emitNewListWhenAdditionalDataIsInsertedInDb() = runBlocking {
        genreMappingDao.saveGenre(listOf(genreData))
        genreMappingDao.getAllGenre().test {
            val result = awaitItem()
            assert(result == listOf(genreData))
            genreMappingDao.saveGenre(listOf(genreData2))
            val result2 = awaitItem()
            assert(result2 == listOf(genreData, genreData2))
        }
    }

    @Test
    @Throws(Exception::class)
    fun emitNewListWhenDataIsUpdatedInDb() = runBlocking {
        genreMappingDao.saveGenre(listOf(genreData))
        genreMappingDao.getAllGenre().test {
            val result = awaitItem()
            assert(result == listOf(genreData))
            genreMappingDao.saveGenre(listOf(genreData3))
            val result2 = awaitItem()
            assert(result2 == listOf(genreData3))
        }
    }


    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

}