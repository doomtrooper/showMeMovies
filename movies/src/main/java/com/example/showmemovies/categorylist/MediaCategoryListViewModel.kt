package com.example.showmemovies.categorylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showmemovies.IODispatcher
import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.TVMEDIACATEGORY
import com.example.showmemovies.repository.IGenreRepository
import com.example.showmemovies.repository.IHomeFeedsRepository
import com.example.showmemovies.repository.IMediaListRepository
import com.example.showmemovies.repository.ITvGenreRepository
import com.example.showmemovies.utils.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MediaCategoryListViewModel.Factory::class)
class MediaCategoryListViewModel @AssistedInject constructor(
    @Assisted private val movieCategory: MEDIACATEGORY,
    @Assisted private val tvCategory: TVMEDIACATEGORY,
    private val mediaListRepository: IMediaListRepository,
    val tvGenreRepository: ITvGenreRepository,
    val genreRepository: IGenreRepository,
    @IODispatcher val dispatcher: CoroutineDispatcher
) : ViewModel() {

    var uiState = MutableStateFlow(MediaCategoryListUiState())

    init {
        viewModelScope.launch {
            launch(dispatcher) { observeMedia(mediaListRepository) }
            launch(dispatcher) { observeTvMedia(mediaListRepository) }
        }
    }

    private suspend fun observeMedia(repository: IMediaListRepository) {
        repository.flowMoviesFromDb(movieCategory).collect { result ->
            when (result) {
                is Result.Success -> {
                    uiState.update {
                        uiState.value.copy(
                            movieMedia = result.body
                        )
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun observeTvMedia(repository: IMediaListRepository) {
        repository.flowTvMediaFromDb(tvCategory).collect { result ->
            when (result) {
                is Result.Success -> {
                    uiState.update {
                        uiState.value.copy(
                            tvMedia = result.body
                        )
                    }
                }
                else -> {}
            }
        }
    }


    @AssistedFactory
    interface Factory {
        fun create(
            movieCategory: MEDIACATEGORY,
            tvCategory: TVMEDIACATEGORY,
        ): MediaCategoryListViewModel
    }
}