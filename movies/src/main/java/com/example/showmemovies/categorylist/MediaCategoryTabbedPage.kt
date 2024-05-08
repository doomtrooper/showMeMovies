package com.example.showmemovies.categorylist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.TvModelWithGenres
import kotlinx.coroutines.launch

@Composable
fun MediaCategoryTabbedPage(state: MediaCategoryListUiState, loadNextMoviePage: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TabScreen(state, loadNextMoviePage)
    }
}

@Composable
private fun MovieTab(
    state: MediaCategoryListUiState,
    buffer: Int,
    loadNextMoviePage: () -> Unit
) {
    if (state.movieMedia.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp)
            )
        }
    } else {
        val lazyListState = rememberLazyListState()
        LazyColumn(state = lazyListState) {
            items(state.movieMedia.size) {
                MediaCard(mediaData = state.movieMedia[it].toMediaCard())
            }
            if (state.loadingMovieMedia) {
                item {
                    if (state.loadingMovieMedia) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.width(64.dp)
                            )
                        }
                    }
                }
            }
        }
        val firstItemVisibleIndex =
            remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
        val shouldFetchMoreItems = remember(firstItemVisibleIndex.value) {
            val totalItemsCount = lazyListState.layoutInfo.totalItemsCount
            if (totalItemsCount == 0) return@remember false
            println("compose totalItemsCount: $totalItemsCount")
            println("compose state.movieMedia.size: ${state.movieMedia.size}")
            val lastVisibleIndex =
                (lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            println("compose lastVisibleIndex: $lastVisibleIndex")
            totalItemsCount - lastVisibleIndex < buffer
        }
        LaunchedEffect(key1 = shouldFetchMoreItems) {
            println("compose shouldFetchMoreItems: $shouldFetchMoreItems")
            if (shouldFetchMoreItems) {
                loadNextMoviePage()
            }
        }
    }
}

@Composable
private fun TvTab(state: MediaCategoryListUiState) {
    LazyColumn {
        items(state.tvMedia.size) {
            MediaCard(mediaData = state.tvMedia[it].toMediaCard())
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabScreen(state: MediaCategoryListUiState, loadNextMoviePage: () -> Unit) {
    val tabs = listOf("Movie", "Tv")
    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    }
                )
            }
        }
        HorizontalPager(state = pagerState) {
            when (it) {
                0 -> MovieTab(state = state, 4, loadNextMoviePage)
                1 -> TvTab(state = state)
            }
        }
    }
}


@Composable
fun MediaCard(mediaData: MediaData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Card(elevation = 2.dp) {
            Row {
                AsyncImage(
                    modifier = Modifier
                        .weight(.2f)
                        .height(100.dp),
                    model = "https://image.tmdb.org/t/p/w500/" + mediaData.posterPath,
                    contentDescription = mediaData.title,
                    contentScale = ContentScale.FillWidth
                )
                Box(modifier = Modifier.weight(.8f)) {
                    Column {
                        Text(
                            text = mediaData.title,
                            color = Color.Black,
                            fontSize = 18.sp,
                            maxLines = 1,
                            lineHeight = 20.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = mediaData.description,
                            color = Color.Black,
                            fontSize = 18.sp,
                            maxLines = 1,
                            lineHeight = 20.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = mediaData.releaseYear,
                            color = Color.Black,
                            fontSize = 18.sp,
                            maxLines = 1,
                            lineHeight = 20.sp,
                            overflow = TextOverflow.Ellipsis
                        )

                    }
                }
            }
        }
    }

}

data class MediaData(
    val title: String,
    val posterPath: String,
    val description: String,
    val releaseYear: String,
    val genres: List<String>
)

private fun MovieModelWithGenres.toMediaCard(): MediaData {
    return MediaData(
        mediaModel.title,
        mediaModel.posterPath,
        mediaModel.overview,
        mediaModel.releaseDate,
        listOf()
    )
}

private fun TvModelWithGenres.toMediaCard(): MediaData {
    return MediaData(
        mediaModel.title,
        mediaModel.posterPath,
        mediaModel.overview,
        mediaModel.releaseDate,
        listOf()
    )
}
