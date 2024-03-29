package com.example.showmemovies

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showmemovies.models.MEDIACATEGORY.*
import com.example.showmemovies.utils.NetworkResponseWrapper.*
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.repository.IGenreRepository
import com.example.showmemovies.repository.ITrendingMoviesRepository
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
    private val genreRepository: IGenreRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    // Expose screen UI state
    var uiState = MutableStateFlow(MovieHomePageUiState(loading = true))

    init {
        viewModelScope.launch {
            launch(dispatcher) { observeMedia() }
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
            launch(dispatcher) { genreRepository.fetchGenreFromNetwork() }
        }
    }

    private suspend fun observeMedia() {
        repository.flowTrendingMoviesFromDb()
            .collect { result ->
                when (result) {
                    is Result.Error -> setNetworkResponseInUiState(result.body)
                    is Result.Success -> {
                        uiState.update {
                            uiState.value.copy(
                                all = result.body,
                                trendingMovies = result.body.filter { it.mediaModel.mediaCategory == TRENDING_ALL },
                                topRatedMovies = result.body.filter { it.mediaModel.mediaCategory == TOP_RATED_MOVIE },
                                topRatedTv = result.body.filter { it.mediaModel.mediaCategory == TOP_RATED_TV },
                                popularMovies = result.body.filter { it.mediaModel.mediaCategory == POPULAR_MOVIE },
                                popularTv = result.body.filter { it.mediaModel.mediaCategory == POPULAR_TV },
                            )
                        }
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