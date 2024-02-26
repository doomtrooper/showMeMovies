package com.example.showmemovies

import com.example.showmemovies.datasource.ITendingMoviesNetworkDataSource
import com.example.showmemovies.datasource.MoviesApi
import com.example.showmemovies.datasource.TendingMoviesNetworkDataSource
import com.example.showmemovies.repository.ITrendingMoviesRepository
import com.example.showmemovies.repository.TrendingMoviesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@InstallIn(ViewModelComponent::class)
@Module
class HiltModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder().apply {
            addInterceptor(
                Interceptor { chain ->
                    val builder = chain.request().newBuilder()
                    builder.header("accept", "application/json")
                    builder.header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxMjMzZDVkMWQ1ZTRhNTFlOWZhYTVmYWY4ZjkxMGQ0NyIsInN1YiI6IjY1ZDU2ODYyMjVjZDg1MDE4NjdlMWRlYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.2Wm0Uf36zDwdlJjV4MDhdoATaegMIQ-Ch4ws1T32bgE")
                    return@Interceptor chain.proceed(builder.build())
                }
            )
            addInterceptor(httpLoggingInterceptor)
        }.build()
    }

    @Provides
    fun provideRetrofitInstance(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    fun providesMoviesApi(retrofit: Retrofit): MoviesApi = retrofit.create(MoviesApi::class.java)

    @Provides
    fun data(moviesApi: MoviesApi): ITendingMoviesNetworkDataSource =
        TendingMoviesNetworkDataSource(moviesApi)

    @Provides
    fun repository(trendingDataSource: ITendingMoviesNetworkDataSource): ITrendingMoviesRepository =
        TrendingMoviesRepository(trendingDataSource)
}