package com.example.showmemovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showmemovies.NetworkResponseWrapper.*
import com.example.showmemovies.repository.ITrendingMoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieHomePageViewModel @Inject constructor(repository: ITrendingMoviesRepository) :
    ViewModel() {
    // Expose screen UI state
    var uiState = MutableStateFlow(MovieHomePageUiState())

    init {
        viewModelScope.launch {
            uiState.update {
                uiState.value.copy(loading = true)
            }
            val fetchTrendingMovies = repository.fetchTrendingMovies()
            println(fetchTrendingMovies)
            when (fetchTrendingMovies) {
                is Success -> uiState.update {
                    uiState.value.copy(
                        trendingMovies = fetchTrendingMovies.body.movieList,
                        loading = false
                    )
                }

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
            }

        }
    }
}