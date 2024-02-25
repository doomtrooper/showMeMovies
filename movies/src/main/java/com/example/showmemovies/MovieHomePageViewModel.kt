package com.example.showmemovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showmemovies.repository.ITrendingMoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieHomePageViewModel @Inject constructor(repository: ITrendingMoviesRepository): ViewModel() {
    // Expose screen UI state
    private val _uiState = MutableStateFlow(MovieHomePageUiState())
    val uiState: StateFlow<MovieHomePageUiState> = _uiState.asStateFlow()
    init {
        viewModelScope.launch {
            repository.fetchTrendingMovies()
        }
    }
}