package com.example.showmemovies

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.showmemovies.datasource.dao.LocationDao
import com.example.showmemovies.models.LocationModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LocationForeGroundService : Service() {

    @Inject
    lateinit var locationDao: LocationDao
    private var locationCallback: LocationCallback? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationHandlerThread: HandlerThread? = null
    private var locationHandler: Handler? = null
    private var locationLooper: Looper? = null
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(job + Dispatchers.IO)
    private var sharedLocationFlow: MutableSharedFlow<LocationModel> = MutableSharedFlow()
    private var binder: LocalBinder? = LocalBinder(sharedLocationFlow)


    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("onStartCommand: " + locationHandler?.toString())
        locationHandler?.obtainMessage(1).also {
            println("Sending msg: " + it?.what)
            it?.arg1 = startId
            it?.let {
                locationHandler?.sendMessage(it)
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        setupForegroundService()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setFusedLocationCallback()
        setLocationHandlerThread()
        println("onCreate")
    }

    private fun setupForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground()
        } else {
            startForeground(1, Notification())
        }
    }

    private fun setLocationHandlerThread() {
        locationHandlerThread =
            object : HandlerThread("location-handler-thread", THREAD_PRIORITY_BACKGROUND) {
            }.apply {
                start()
                locationLooper = looper
                locationHandler = ServiceHandler(looper)
            }
    }

    private fun setFusedLocationCallback() {
        locationCallback = MyLocationCallback { onLocation(it) }
    }

    private fun onLocation(locationResult: LocationResult) {
        for (location in locationResult.locations) {
            println(location.toString())
            locationDao.insertLocation(
                LocationModel(
                    latitude = location.latitude,
                    longitude = location.longitude
                ).also {
                    coroutineScope.launch {
                        sharedLocationFlow.emit(it)
                    }
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy")
        locationCallback?.let { fusedLocationProviderClient.removeLocationUpdates(it) }
        locationCallback = null
        locationLooper = null
        locationHandler = null
        locationHandlerThread?.quit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "com.example.showmemovies"
        val channelName = "My Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        return resultCode == ConnectionResult.SUCCESS
    }

    /*
    * Each Handler instance is associated with a single thread and that thread's message queue.
    * When you create a new Handler it is bound to a Looper.
    * It will deliver messages and runnables to that Looper's message queue
    * and execute them on that Looper's thread.
     */
    inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            println("Handling msg: " + msg.what)
            if (1 == msg.what) {
                if (ActivityCompat.checkSelfPermission(
                        this@LocationForeGroundService.applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    && isGooglePlayServicesAvailable(this@LocationForeGroundService.applicationContext)
                ) {
                    println(this@LocationForeGroundService.locationCallback.toString())
                    locationCallback?.let {
                        println("fetching location...")
                        fusedLocationProviderClient.requestLocationUpdates(
                            LocationRequest.Builder(1000)
                                .build(),
                            it,
                            locationLooper
                        )
                    }
                }
            }
        }
    }

    class LocalBinder(private val locationFlow : MutableSharedFlow<LocationModel>) : Binder() {
        fun getLocationFlow(): MutableSharedFlow<LocationModel> = locationFlow
    }

}