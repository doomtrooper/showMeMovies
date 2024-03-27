package com.example.showmemovies

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_ANY
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.example.showmemovies.models.GenreNameIdMapping
import com.example.showmemovies.models.MediaModel
import com.example.showmemovies.models.MovieIdGenreIdMapping
import com.example.showmemovies.models.MovieModelWithGenres
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieHomePage(state: MovieHomePageUiState) {
    println(state)
    /*
    val lifeCycleEvent = rememberLifeCycleEvent()
    println("mutableLifeCycleEvent: ${lifeCycleEvent.value}")
    if (lifeCycleEvent.value == ON_START) {
        LaunchedEffect(key1 = lifeCycleEvent) {
            println()
        }
    }
    */
    if (state.trendingMovies.isNotEmpty()) {
        val pagerState = rememberPagerState(pageCount = {
            state.trendingMovies.size
        })
        val gradientColors = listOf(Cyan, Magenta, Yellow)

        Box {
            LazyColumn {
                item {
                    Text(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp),
                        text = "Trending Movies",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        )
                    )
                }
                item {
                    HorizontalPager(
                        state = pagerState,
                        pageSpacing = 10.dp,
                        contentPadding = PaddingValues(
                            horizontal = 32.dp,
                            vertical = 8.dp
                        ),
                    ) {
                        MovieCard(
                            movieModelWithGenres = state.trendingMovies[it],
                            pagerState,
                            it,
                            state.genreIdMapping
                        )
                    }
                }
                item {
                    Text(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp),
                        text = "Top rated Movies",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        )
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        items(state.topRatedMovies.size) {
                            MediaCard(
                                movieModelWithGenres = state.topRatedMovies[it],
                                genreIdMapping = state.genreIdMapping
                            )
                        }
                    }
                }
                item {
                    Text(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp),
                        text = "Top rated Tv",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        )
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        items(state.topRatedTv.size) {
                            MediaCard(
                                movieModelWithGenres = state.topRatedTv[it],
                                genreIdMapping = state.genreIdMapping
                            )
                        }
                    }
                }
                item {
                    Text(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp),
                        text = "Popular movies",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        )
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        items(state.popularMovies.size) {
                            MediaCard(
                                movieModelWithGenres = state.popularMovies[it],
                                genreIdMapping = state.genreIdMapping
                            )
                        }
                    }
                }
                item {
                    Text(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp),
                        text = "Popular TV",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        )
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        items(state.popularTv.size) {
                            MediaCard(
                                movieModelWithGenres = state.popularTv[it],
                                genreIdMapping = state.genreIdMapping
                            )
                        }
                    }
                }
            }
        }
    } else {
        HomeScreen()
    }
}


@Composable
private fun MediaCard(
    movieModelWithGenres: MovieModelWithGenres,
    genreIdMapping: Map<Long, String>
) {
    Card(elevation = 2.dp, border = BorderStroke(1.dp, Color.Black)) {
        Box {
            Column(Modifier.width(150.dp)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500/" + movieModelWithGenres.mediaModel.posterPath,
                        contentDescription = movieModelWithGenres.mediaModel.title,
                        contentScale = ContentScale.FillWidth
                    )
                }
                Box(modifier = Modifier.padding(4.dp)) {
                    Text(
                        text = movieModelWithGenres.mediaModel.title,
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

@Preview
@Composable
private fun PreviewMediaCard() {
    MediaCard(
        movieModelWithGenres = MovieModelWithGenres(mediaModel, movieIdGenreIdMappings),
        genreIdMapping = mapOf(1L to "ABCD")
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MovieCard(
    movieModelWithGenres: MovieModelWithGenres,
    pagerState: PagerState,
    page: Int,
    genreIdMapping: Map<Long, String>
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .graphicsLayer {
                // Calculate the absolute offset for the current page from the
                // scroll position. We use the absolute value which allows us to mirror
                // any effects for both directions
                val pageOffset = (
                        (pagerState.currentPage - page) + pagerState
                            .currentPageOffsetFraction
                        ).absoluteValue

                // We animate the alpha, between 50% and 100%
                alpha = lerp(
                    start = 0.5f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
            }
            .height(200.dp),
        elevation = 10.dp
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500/" + movieModelWithGenres.mediaModel.posterPath,
            contentDescription = movieModelWithGenres.mediaModel.title,
            contentScale = ContentScale.FillWidth
        )
        Box(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Column(Modifier.align(alignment = Alignment.BottomStart)) {
                Text(
                    text = movieModelWithGenres.mediaModel.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(top = 2.dp)
                ) {
                    items(
                        movieModelWithGenres.genreIdMapping.size,
                        { index -> movieModelWithGenres.genreIdMapping[index].genreId }) {
                        Text(
                            text = genreIdMapping[(movieModelWithGenres.genreIdMapping[it].genreId)]
                                ?: "",
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun rememberLifeCycleEvent(): MutableState<Lifecycle.Event> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val mutableLifeCycleEvent = remember { mutableStateOf(ON_ANY) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            println(event.name)
            mutableLifeCycleEvent.value = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            println("onDispose")
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    return mutableLifeCycleEvent
}


@Composable
fun HomeScreen() {
    // this is the most outer box that will
    // contain all the views,buttons,chips,etc.
    Box(
        modifier = Modifier
            .background(Color.Blue)
            .fillMaxSize()
    ) {
        Column {
            // this is how we call
            // function adding whole UI
            GreetingSection()
        }
    }
}

@Composable
fun GreetingSection(
    name: String = "Geeks"
) {
    // here we just arrange the views
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            // heading text view
            Text(
                text = "Good morning, $name", style = MaterialTheme.typography.h1
            )
            Text(
                text = "We wish you have a good day!", style = MaterialTheme.typography.body1
            )
        }
    }
}

private val mediaModel: MediaModel = MediaModel(
    false,
    "/44immBwzhDVyjn87b3x3l9mlhAD.jpg",
    934433,
    "Scream VI",
    "en",
    "Scream VI",
    "Following the latest Ghostface killings, the four survivors leave Woodsboro behind and start a fresh chapter.",
    "/wDWwtvkRRlgTiUr6TyLSMX8FCuZ.jpg",
    "movie",
    609.941,
    "2023-03-08",
    false,
    7.374,
    684
)

private val movieIdGenreIdMappings = listOf(MovieIdGenreIdMapping(movieId = mediaModel.id, 1L))