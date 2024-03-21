package com.example.showmemovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

/*
* Once Hilt is set up in your Application class and an application-level component is available,
* Hilt can provide dependencies to other Android classes that have the @AndroidEntryPoint annotation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
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
        val state by homePageViewModel.uiState.collectAsStateWithLifecycle(
            lifecycleOwner = LocalLifecycleOwner.current,
            minActiveState = Lifecycle.State.CREATED
        )
        MovieHomePage(state)
    }
}