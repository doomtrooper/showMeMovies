package com.example.showmemovies

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.utils.NetworkResponseWrapper.*
import com.example.showmemovies.models.MediaResponseContainer
import com.example.showmemovies.repository.IGenreRepository
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
    private val genreRepository: IGenreRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    // Expose screen UI state
    var uiState = MutableStateFlow(MovieHomePageUiState(loading = true))

    init {
        viewModelScope.launch {
            launch(dispatcher) { observeMedia(MEDIACATEGORY.TRENDING_ALL) }
            launch(dispatcher) { observeMedia(MEDIACATEGORY.TOP_RATED_MOVIE) }
            launch(dispatcher) { observeMedia(MEDIACATEGORY.TOP_RATED_TV) }
            launch(dispatcher) { makeNetworkCall(MEDIACATEGORY.TRENDING_ALL) }
            launch(dispatcher) { makeNetworkCall(MEDIACATEGORY.TOP_RATED_MOVIE) }
            launch(dispatcher) { makeNetworkCall(MEDIACATEGORY.TOP_RATED_TV) }
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

    private suspend fun makeNetworkCall(mediaCategory: MEDIACATEGORY) {
        val networkResponseWrapper: NetworkResponseWrapper<MediaResponseContainer>? =
            repository.fetchTrendingMoviesFromNetwork(mediaCategory)
                .takeIf { it !is Success }
        networkResponseWrapper?.let {
            withContext(Dispatchers.Main) {
                setNetworkResponseInUiState(it)
            }
        }
    }

    private suspend fun observeMedia(mediaCategory: MEDIACATEGORY) {
        repository.flowTrendingMoviesFromDb(mediaCategory)
            .collect { mediaModelsWithGenres ->
                withContext(Dispatchers.Main) {
                    uiState.update {
                        when (mediaCategory) {
                            MEDIACATEGORY.TRENDING_ALL -> uiState.value.copy(trendingMovies = mediaModelsWithGenres)
                            MEDIACATEGORY.TOP_RATED_MOVIE -> uiState.value.copy(topRatedMovies = mediaModelsWithGenres)
                            MEDIACATEGORY.TOP_RATED_TV -> uiState.value.copy(topRatedTv = mediaModelsWithGenres)
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