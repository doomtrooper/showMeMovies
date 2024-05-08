package com.example.showmemovies

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.showmemovies.categorylist.MediaCategoryListViewModel
import com.example.showmemovies.categorylist.MediaCategoryTabbedPage
import com.example.showmemovies.homefeed.MovieHomePage
import com.example.showmemovies.homefeed.MovieHomePageViewModel
import com.example.showmemovies.locationservice.LocationForeGroundService
import com.example.showmemovies.models.MEDIACATEGORY
import com.example.showmemovies.models.TVMEDIACATEGORY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val movieCategory: String = "movieCategory"
const val tvCategory: String = "tvCategory"

/*
* Once Hilt is set up in your Application class and an application-level component is available,
* Hilt can provide dependencies to other Android classes that have the @AndroidEntryPoint annotation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var serviceStarted = false
    private val readFineLocationPermission = ACCESS_FINE_LOCATION
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(job + Dispatchers.IO)
    private var mBound: Boolean = false


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val postNotificationPermission = POST_NOTIFICATIONS
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationForeGroundService.LocalBinder
            coroutineScope.launch {
                binder.getLocationFlow().collect {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            it.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }

    }
    private var activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsMap ->
            if (true == permissionsMap[readFineLocationPermission]) {
                toggleService(serviceStarted)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                bottomBar = {
                    BottomAppBar {
                        IconButton(onClick = { bindToLocationService() }) {
                            Icon(
                                Icons.Filled.AddCircle,
                                contentDescription = "Localized description"
                            )
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

    private fun bindToLocationService() {
        if (mBound) {
            unbindService(serviceConnection)
            mBound = false
        } else {
            Intent(this@MainActivity, LocationForeGroundService::class.java).also {
                bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
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
                if (mBound) {
                    unbindService(serviceConnection)
                    mBound = false
                }
                this@MainActivity.stopService(intent)
            }
            this.serviceStarted = !this.serviceStarted
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activityResultLauncher.launch(
                    arrayOf(
                        readFineLocationPermission,
                        postNotificationPermission
                    )
                )
            } else {
                activityResultLauncher.launch(arrayOf(readFineLocationPermission))
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            unbindService(serviceConnection)
            mBound = false
        }
    }
}


// Define the MyApp composable, including the `NavController` and `NavHost`.
@Composable
fun MyApp(paddingValues: PaddingValues) {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = "home",
        modifier = Modifier.padding(paddingValues)
    ) {
        homePage() { mediaCategory, tvCategory -> navController.navigate("mediaTabbed/$mediaCategory/$tvCategory") }
        mediaCategoryTabs()
    }
}

fun NavGraphBuilder.homePage(navigateToTabbedList: (mediaCategory: MEDIACATEGORY, tvCategory: TVMEDIACATEGORY) -> Unit) {
    composable(route = "home") {
        val homePageViewModel: MovieHomePageViewModel = hiltViewModel<MovieHomePageViewModel>()
        val state by homePageViewModel.uiState.collectAsStateWithLifecycle(
            lifecycleOwner = LocalLifecycleOwner.current,
            minActiveState = Lifecycle.State.CREATED
        )
        MovieHomePage(state, navigateToTabbedList)
    }
}

fun NavGraphBuilder.mediaCategoryTabs() {
    composable(route = "mediaTabbed/{$movieCategory}/{$tvCategory}", arguments = listOf(
        navArgument(movieCategory) {
            type = NavType.StringType
            defaultValue = MEDIACATEGORY.UPCOMING_MOVIE.name
        },
        navArgument(tvCategory) {
            type = NavType.StringType
            nullable = false
            defaultValue = TVMEDIACATEGORY.POPULAR_TV.name
        }
    )) { backstack ->
        val movieCategory =
            MEDIACATEGORY.valueOf(
                backstack.arguments?.getString(movieCategory) ?: MEDIACATEGORY.UPCOMING_MOVIE.name
            )
        val tvCategory =
            TVMEDIACATEGORY.valueOf(
                backstack.arguments?.getString(tvCategory) ?: TVMEDIACATEGORY.POPULAR_TV.name
            )
        val mediaCategoryListViewModel =
            hiltViewModel<MediaCategoryListViewModel, MediaCategoryListViewModel.Factory>(
                creationCallback = { factory -> factory.create(movieCategory, tvCategory) }
            )
        val state by mediaCategoryListViewModel.uiState.collectAsStateWithLifecycle()
        MediaCategoryTabbedPage(state) { mediaCategoryListViewModel.loadNextPage() }
    }
}