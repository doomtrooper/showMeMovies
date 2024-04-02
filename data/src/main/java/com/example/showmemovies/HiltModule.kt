package com.example.showmemovies

import android.content.Context
import androidx.room.Room
import com.example.showmemovies.datasource.dao.GenreMappingDao
import com.example.showmemovies.datasource.dao.MediaCategoryDao
import com.example.showmemovies.datasource.dao.MovieDao
import com.example.showmemovies.datasource.dao.MovieIdGenreIdMappingDao
import com.example.showmemovies.datasource.dao.TvDao
import com.example.showmemovies.datasource.dao.TvGenreMappingDao
import com.example.showmemovies.datasource.dao.TvIdGenreIdMappingDao
import com.example.showmemovies.datasource.dao.TvMediaCategoryDao
import com.example.showmemovies.datasource.network.GenreNetworkDataSource
import com.example.showmemovies.datasource.network.IGenreNetworkDataSource
import com.example.showmemovies.datasource.network.ITendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.network.ITvGenreNetworkDataSource
import com.example.showmemovies.datasource.network.TendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.network.TvGenreNetworkDataSource
import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.models.TVMEDIACATEGORY
import com.example.showmemovies.repository.HomeFeedsRepository
import com.example.showmemovies.repository.GenreRepository
import com.example.showmemovies.repository.IGenreRepository
import com.example.showmemovies.repository.IHomeFeedsRepository
import com.example.showmemovies.repository.ITvGenreRepository
import com.example.showmemovies.repository.TvGenreRepository
import com.example.showmemovies.utils.NetworkResponseWrapperCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class HiltModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder().apply {
            addInterceptor(
                Interceptor { chain ->
                    val builder = chain.request().newBuilder()
                    builder.header("accept", "application/json")
                    builder.header(
                        "Authorization",
                        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxMjMzZDVkMWQ1ZTRhNTFlOWZhYTVmYWY4ZjkxMGQ0NyIsInN1YiI6IjY1ZDU2ODYyMjVjZDg1MDE4NjdlMWRlYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.2Wm0Uf36zDwdlJjV4MDhdoATaegMIQ-Ch4ws1T32bgE"
                    )
                    return@Interceptor chain.proceed(builder.build())
                }
            )
            addInterceptor(httpLoggingInterceptor)
        }.build()
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(NetworkResponseWrapperCallAdapterFactory())
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun providesMoviesApi(retrofit: Retrofit): MoviesApi = retrofit.create(MoviesApi::class.java)

    @Singleton
    @Provides
    fun data(moviesApi: MoviesApi): ITendingMoviesNetworkDataSource =
        TendingMoviesNetworkDataSource(moviesApi)

    @Singleton
    @Provides
    fun provideGenreNetworkDatasource(moviesApi: MoviesApi): IGenreNetworkDataSource =
        GenreNetworkDataSource(moviesApi)

    @Singleton
    @Provides
    fun database(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "show-me-movies"
        ).build()
    }

    @Singleton
    @Provides
    fun moviesDao(appDatabase: AppDatabase): MovieDao {
        return appDatabase.moviesDao()
    }

    @Singleton
    @Provides
    fun tvDao(appDatabase: AppDatabase): TvDao {
        return appDatabase.tvDao()
    }

    @Singleton
    @Provides
    fun mediaCategory(appDatabase: AppDatabase): MediaCategoryDao {
        return appDatabase.mediaCategoryDao()
    }

    @Singleton
    @Provides
    fun providesMovieGenreMappingDao(appDatabase: AppDatabase): MovieIdGenreIdMappingDao {
        return appDatabase.movieIdGenreIdMappingDao()
    }

    @Singleton
    @Provides
    fun providesGenreMappingDao(appDatabase: AppDatabase): GenreMappingDao {
        return appDatabase.genreDao()
    }

    @Singleton
    @Provides
    fun providesGenreRepository(
        genreNetworkDataSource: IGenreNetworkDataSource,
        genreMappingDao: GenreMappingDao
    ): IGenreRepository = GenreRepository(genreNetworkDataSource, genreMappingDao)

    @Singleton
    @Provides
    fun provideFeedMovieMediaApiMapper(moviesApi: MoviesApi): FeedApiMapper {
        return FeedApiMapper(
            enumValues<MEDIACATEGORY>().associateWith {
                when (it) {
                    MEDIACATEGORY.TRENDING_MOVIE -> suspend { moviesApi.trendingMovie() }
                    MEDIACATEGORY.TOP_RATED_MOVIE -> suspend { moviesApi.popularMovie() }
                    MEDIACATEGORY.POPULAR_MOVIE -> suspend { moviesApi.topRatedMovie() }
                    MEDIACATEGORY.UPCOMING_MOVIE -> suspend { moviesApi.topRatedMovie() }
                }
            },
            enumValues<TVMEDIACATEGORY>().associateWith {
                when (it) {
                    TVMEDIACATEGORY.TRENDING_TV -> suspend { moviesApi.trendingTv() }
                    TVMEDIACATEGORY.POPULAR_TV -> suspend { moviesApi.popularTv() }
                    TVMEDIACATEGORY.TOP_RATED_TV -> suspend { moviesApi.topRatedTv() }
                }
            }
        )
    }

    @Singleton
    @Provides
    fun providesTvIdGenreIdMappingDao(appDatabase: AppDatabase): TvIdGenreIdMappingDao {
        return appDatabase.tvIdGenreIdMappingDao()
    }

    @Singleton
    @Provides
    fun providesTvMediaCategoryDao(appDatabase: AppDatabase): TvMediaCategoryDao {
        return appDatabase.tvMediaCategoryDao()
    }

    @Singleton
    @Provides
    fun providesHomeFeedsRepository(
        trendingDataSource: ITendingMoviesNetworkDataSource,
        movieDao: MovieDao,
        tvDao: TvDao,
        mediaCategoryDao: MediaCategoryDao,
        tvMediaCategoryDao: TvMediaCategoryDao,
        movieIdGenreIdMappingDao: MovieIdGenreIdMappingDao,
        tvIdGenreIdMappingDao: TvIdGenreIdMappingDao,
        feedApiMapper: FeedApiMapper,
    ): IHomeFeedsRepository =
        HomeFeedsRepository(
            trendingDataSource,
            movieDao,
            tvDao,
            mediaCategoryDao,
            tvMediaCategoryDao,
            movieIdGenreIdMappingDao,
            tvIdGenreIdMappingDao,
            feedApiMapper
        )


    @Singleton
    @Provides
    fun providesTvGenreNetworkDatasource(moviesApi: MoviesApi): ITvGenreNetworkDataSource {
        return TvGenreNetworkDataSource(moviesApi)
    }

    @Singleton
    @Provides
    fun providesTvGenreDao(appDatabase: AppDatabase) = appDatabase.tvGenreDao()

    @Singleton
    @Provides
    fun providesTvGenreRepository(
        genreNetworkDataSource: ITvGenreNetworkDataSource,
        tvGenreDao: TvGenreMappingDao
    ): ITvGenreRepository {
        return TvGenreRepository(genreNetworkDataSource, tvGenreDao)
    }

    @Singleton
    @Provides
    @IODispatcher
    fun providerIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Singleton
    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher