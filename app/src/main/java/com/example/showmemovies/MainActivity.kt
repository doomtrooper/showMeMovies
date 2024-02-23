package com.example.showmemovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
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
        composable("movies") { MovieHomePage() }
    }
}