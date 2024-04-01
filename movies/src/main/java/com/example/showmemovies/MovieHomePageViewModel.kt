package com.example.showmemovies

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showmemovies.models.MEDIACATEGORY.*
import com.example.showmemovies.utils.NetworkResponseWrapper.*
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.TVMEDIACATEGORY
import com.example.showmemovies.models.TVMEDIACATEGORY.*
import com.example.showmemovies.models.TvMediaResponseContainer
import com.example.showmemovies.models.TvModelWithGenres
import com.example.showmemovies.repository.IGenreRepository
import com.example.showmemovies.repository.ITrendingMoviesRepository
import com.example.showmemovies.repository.ITvGenreRepository
import com.example.showmemovies.utils.NetworkResponseWrapper
import com.example.showmemovies.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieHomePageViewModel @Inject constructor(
    private val repository: ITrendingMoviesRepository,
    private val tvGenreRepository: ITvGenreRepository,
    private val genreRepository: IGenreRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    // Expose screen UI state
    var uiState = MutableStateFlow(MovieHomePageUiState(loading = true))

    init {
        viewModelScope.launch {
            launch(dispatcher) { observeMedia() }
            launch(dispatcher) { observeTvMedia() }
            launch(dispatcher) {
                genreRepository.flowGenresFromDb().collect { genres ->
                    withContext(Dispatchers.Main) {
                        uiState.update {
                            uiState.value.copy(
                                genreIdMapping = genres.associate { it.genreId to it.genreName }
                            )
                        }
                    }
                }
            }
            launch(dispatcher) {
                tvGenreRepository.flowTvGenresFromDb().collect { genres ->
                    withContext(Dispatchers.Main) {
                        uiState.update {
                            uiState.value.copy(
                                tvGenreIdMapping = genres.associate { it.genreId to it.genreName }
                            )
                        }
                    }
                }
            }
            launch(dispatcher) { genreRepository.fetchGenreFromNetwork() }
            launch(dispatcher) { tvGenreRepository.fetchTvGenreFromNetwork() }
        }
    }

    private suspend fun observeMedia() {
        repository.flowTrendingMoviesFromDb().collect { result ->
            when (result) {
                is Result.Error -> setNetworkResponseInUiState(result.body)
                is Result.Success -> {
                    val trendingMovies = mutableListOf<MovieModelWithGenres>()
                    val upcomingMovies = mutableListOf<MovieModelWithGenres>()
                    val topRatedMovies = mutableListOf<MovieModelWithGenres>()
                    val popularMovies = mutableListOf<MovieModelWithGenres>()
                    result.body.forEach { movieModelWithGenre: MovieModelWithGenres ->
                        movieModelWithGenre.mediaCategoryMapping.forEach {
                            when (it.category) {
                                POPULAR_MOVIE -> popularMovies.add(movieModelWithGenre)
                                TRENDING_MOVIE -> trendingMovies.add(movieModelWithGenre)
                                TOP_RATED_MOVIE -> topRatedMovies.add(movieModelWithGenre)
                                UPCOMING_MOVIE -> upcomingMovies.add(movieModelWithGenre)
                            }
                        }
                    }
                    uiState.update {
                        uiState.value.copy(
                            topRatedMovies = topRatedMovies,
                            upcomingMovies = upcomingMovies,
                            trendingMovies = trendingMovies,
                            popularMovies = popularMovies
                        )
                    }
                }
            }
        }
    }

    private suspend fun observeTvMedia() {
        repository.flowTvMediaFromDb().collect { result ->
            when (result) {
                is Result.Success -> {
                    val trendingTvMedia = mutableListOf<TvModelWithGenres>()
                    val topRatedTvMedia = mutableListOf<TvModelWithGenres>()
                    val popularTvMedia = mutableListOf<TvModelWithGenres>()
                    result.body.forEach { movieModelWithGenre: TvModelWithGenres ->
                        movieModelWithGenre.mediaCategoryMapping.forEach {
                            when (it.category) {
                                POPULAR_TV -> popularTvMedia.add(movieModelWithGenre)
                                TRENDING_TV -> trendingTvMedia.add(movieModelWithGenre)
                                TOP_RATED_TV -> topRatedTvMedia.add(movieModelWithGenre)
                            }
                        }
                    }
                    uiState.update {
                        uiState.value.copy(
                            topRatedTv = topRatedTvMedia,
                            trendingTv = trendingTvMedia,
                            popularTv = popularTvMedia
                        )
                    }
                }

                is Result.Error -> {

                }
            }
        }
    }

    @MainThread
    private fun setNetworkResponseInUiState(
        fetchTrendingMovies: NetworkResponseWrapper<MediaResponseContainer>,
    ) {
        when (fetchTrendingMovies) {
            is NetworkError -> uiState.update {
                uiState.value.copy(
                    error = true,
                    loading = false,
                    errorWrapper = ErrorWrapper(t = fetchTrendingMovies.t)
                )
            }

            is ServiceError -> uiState.update {
                uiState.value.copy(
                    error = true,
                    loading = false,
                    errorWrapper = ErrorWrapper(serviceErrorBody = fetchTrendingMovies.errorBody)
                )
            }

            is UnknownError -> uiState.update {
                uiState.value.copy(
                    error = true,
                    loading = false,
                    errorWrapper = ErrorWrapper(t = fetchTrendingMovies.t)
                )
            }

            else -> {}
        }
    }
}