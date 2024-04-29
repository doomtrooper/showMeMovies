package com.example.showmemovies.categorylist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.showmemovies.models.MovieModelWithGenres
import com.example.showmemovies.models.TvModelWithGenres

@Composable
fun MediaCategoryTabbedPage(state: MediaCategoryListUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TabScreen(state)
    }
}

@Composable
private fun MovieTab(state: MediaCategoryListUiState) {
    Column {
        LazyColumn {
            items(state.movieMedia.size) {
                MediaCard(mediaData = state.movieMedia[it].toMediaCard())
            }
        }
    }
}

@Composable
private fun TvTab(state: MediaCategoryListUiState) {
    Column {
        LazyColumn {
            items(state.tvMedia.size) {
                MediaCard(mediaData = state.tvMedia[it].toMediaCard())
            }
        }
    }
}

@Composable
fun TabScreen(state: MediaCategoryListUiState) {
    var tabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf("Movie", "Tv")

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        when (tabIndex) {
            0 -> MovieTab(state = state)
            1 -> TvTab(state = state)
        }
    }
}


@Composable
fun MediaCard(mediaData: MediaData) {
    Card(elevation = 2.dp, border = BorderStroke(1.dp, Color.Black)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row {
                AsyncImage(
                    modifier = Modifier.weight(.2f),
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
