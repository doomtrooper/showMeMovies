package com.example.showmemovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.showmemovies.repository.ITrendingMoviesRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
* Once Hilt is set up in your Application class and an application-level component is available,
* Hilt can provide dependencies to other Android classes that have the @AndroidEntryPoint annotation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    @Inject lateinit var repository: ITrendingMoviesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
//        println(repository.toString())
    }
}


// Define the MyApp composable, including the `NavController` and `NavHost`.
@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "movies") {
        homePage()
    }
}

fun NavGraphBuilder.homePage() {
    composable(route = "movies") {
        val homePageViewModel: MovieHomePageViewModel = hiltViewModel<MovieHomePageViewModel>()
        val state by homePageViewModel.uiState.collectAsStateWithLifecycle()
        MovieHomePage(state)
    }
}