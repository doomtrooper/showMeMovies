package com.example.showmemovies

import android.content.Context
import androidx.room.Room
import com.example.showmemovies.datasource.dao.GenreMappingDao
import com.example.showmemovies.datasource.network.GenreNetworkDataSource
import com.example.showmemovies.datasource.network.IGenreNetworkDataSource
import com.example.showmemovies.datasource.network.ITendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.dao.MovieIdGenreIdMappingDao
import com.example.showmemovies.datasource.network.TendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.dao.TrendingMovieDao
import com.example.showmemovies.repository.GenreRepository
import com.example.showmemovies.repository.IGenreRepository
import com.example.showmemovies.repository.ITrendingMoviesRepository
import com.example.showmemovies.repository.TrendingMoviesRepository
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
import retrofit2.converter.moshi.MoshiConverterFactory
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
        .addConverterFactory(MoshiConverterFactory.create())
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
    fun trendingMoviesDao(appDatabase: AppDatabase): TrendingMovieDao {
        println("trendingMoviesDao: $appDatabase")
        return appDatabase.trendingMoviesDao()
    }

    @Singleton
    @Provides
    fun providesMovieGenreMappingDao(appDatabase: AppDatabase): MovieIdGenreIdMappingDao {
        println("providesMovieGenreMappingDao: $appDatabase")
        return appDatabase.movieIdGenreIdMappingDao()
    }

    @Singleton
    @Provides
    fun providesGenreMappingDao(appDatabase: AppDatabase): GenreMappingDao {
        println("providesGenreMappingDao: $appDatabase")
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
    fun repository(
        trendingDataSource: ITendingMoviesNetworkDataSource,
        trendingMovieDao: TrendingMovieDao,
        movieIdGenreIdMappingDao: MovieIdGenreIdMappingDao
    ): ITrendingMoviesRepository =
        TrendingMoviesRepository(trendingDataSource, trendingMovieDao, movieIdGenreIdMappingDao)

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