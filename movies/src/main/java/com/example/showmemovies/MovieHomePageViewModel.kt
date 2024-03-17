package com.example.showmemovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showmemovies.NetworkResponseWrapper.*
import com.example.showmemovies.datasource.TrendingMovieDao
import com.example.showmemovies.models.TrendingMoviesResponse
import com.example.showmemovies.repository.ITrendingMoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieHomePageViewModel @Inject constructor(
    private val repository: ITrendingMoviesRepository,
    private val trendingMovieDao: TrendingMovieDao
) : ViewModel() {
    // Expose screen UI state
    var uiState = MutableStateFlow(MovieHomePageUiState())

    init {
        viewModelScope.launch {
            val networkResponseDeferred = async(Dispatchers.Default) {
                repository.fetchTrendingMovies()
            }
            launch(Dispatchers.IO) {
                trendingMovieDao.getAllTrendingMovies().collect { trendingMovies ->
                    withContext(Dispatchers.Main) {
                        uiState.update {
                            uiState.value.copy(trendingMovies = trendingMovies, loading = false)
                        }
                    }
                }
            }
            uiState.update {
                uiState.value.copy(loading = true)
            }
            val fetchTrendingMovies = networkResponseDeferred.await()
            setNetworkResponseInUiState(fetchTrendingMovies, trendingMovieDao)
        }
    }

    private fun setNetworkResponseInUiState(
        fetchTrendingMovies: NetworkResponseWrapper<TrendingMoviesResponse>,
        trendingMovieDao: TrendingMovieDao
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
                viewModelScope.launch(Dispatchers.IO) {
                    trendingMovieDao.saveAllTrendingMovies(fetchTrendingMovies.body.movieList)
                }
            }
        }
    }
}