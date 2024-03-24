package com.example.showmemovies

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showmemovies.utils.NetworkResponseWrapper.*
import com.example.showmemovies.models.TrendingMoviesResponse
import com.example.showmemovies.repository.GenreRepository
import com.example.showmemovies.repository.ITrendingMoviesRepository
import com.example.showmemovies.utils.NetworkResponseWrapper
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
    private val genreRepository: GenreRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {
    // Expose screen UI state
    var uiState = MutableStateFlow(MovieHomePageUiState(loading = true))

    init {
        viewModelScope.launch {
            launch(dispatcher) {
                repository.flowTrendingMoviesFromDb().collect { trendingMovieWithGenres ->
                    withContext(Dispatchers.Main) {
                        uiState.update {
                            uiState.value.copy(trendingMovies = trendingMovieWithGenres)
                        }
                    }
                }
            }
            launch(dispatcher) {
                val networkResponseWrapper: NetworkResponseWrapper<TrendingMoviesResponse>? =
                    repository.fetchTrendingMoviesFromNetwork().takeIf { it !is Success }
                networkResponseWrapper?.let {
                    withContext(mainDispatcher) {
                        setNetworkResponseInUiState(it)
                    }
                }
            }
            launch(dispatcher) {
                genreRepository.flowGenresFromDb().collect { genres ->
                    withContext(mainDispatcher) {
                        uiState.update {
                            uiState.value.copy(
                                genreIdMapping = genres.associate { it.genreId to it.genreName }
                            )
                        }
                    }
                }
            }
            launch(dispatcher) {
                genreRepository.fetchGenreFromNetwork()
            }
        }
    }

    @MainThread
    private fun setNetworkResponseInUiState(
        fetchTrendingMovies: NetworkResponseWrapper<TrendingMoviesResponse>,
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