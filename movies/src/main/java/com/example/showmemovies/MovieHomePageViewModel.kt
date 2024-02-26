package com.example.showmemovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            val (page, movieList, totalPages, totalResults) = repository.fetchTrendingMovies()
            println(movieList)
            uiState.update {
                uiState.value.copy(trendingMovies = movieList)
            }
        }
    }
}