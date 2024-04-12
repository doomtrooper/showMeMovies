package com.example.showmemovies

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
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
    private var serviceStarted = false
    private val readFineLocation = ACCESS_FINE_LOCATION
    private var locationPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                toggleService(serviceStarted)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                bottomBar = {
                    BottomAppBar {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(Icons.Filled.Check, contentDescription = "Localized description")
                        }
                        IconButton(onClick = {
                            toggleService(serviceStarted)
                        }) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Localized description",
                            )
                        }
                    }
                }
            ) {
                MyApp(it)
            }
        }
    }

    private fun toggleService(serviceStarted: Boolean) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent =
                Intent(this@MainActivity, LocationForeGroundService::class.java)
            if (!serviceStarted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this@MainActivity.startForegroundService(intent)
                } else {
                    this@MainActivity.startService(intent)
                }
            } else {
                this@MainActivity.stopService(intent)
            }
            this.serviceStarted = !this.serviceStarted
        } else {
            locationPermissionResult.launch(readFineLocation)
        }
    }
}


// Define the MyApp composable, including the `NavController` and `NavHost`.
@Composable
fun MyApp(paddingValues: PaddingValues) {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = "movies",
        modifier = Modifier.padding(paddingValues)
    ) {
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