package com.example.showmemovies

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showmemovies.NetworkResponseWrapper.*
import com.example.showmemovies.models.TrendingMoviesResponse
import com.example.showmemovies.repository.ITrendingMoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieHomePageViewModel @Inject constructor(
    private val repository: ITrendingMoviesRepository,
) : ViewModel() {
    // Expose screen UI state
    var uiState = MutableStateFlow(MovieHomePageUiState())

    init {
        viewModelScope.launch {
            uiState.update {
                uiState.value.copy(loading = true)
            }
            launch(Dispatchers.IO) {
                repository.fetchTrendingMovies().collect {
                    withContext(Dispatchers.Main) {
                        setNetworkResponseInUiState(it)
                    }
                }
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

            is Success -> {
                uiState.update {
                    uiState.value.copy(
                        error = false,
                        loading = false,
                        trendingMovies = fetchTrendingMovies.body.movieList
                    )
                }
            }
        }
    }
}