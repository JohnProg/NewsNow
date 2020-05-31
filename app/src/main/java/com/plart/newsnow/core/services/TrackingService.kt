package com.plart.newsnow.core.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.plart.newsnow.R
import com.plart.newsnow.core.utils.Constants.Companion.LOCATION_EXTRA
import com.plart.newsnow.core.utils.Constants.Companion.LOCATION_UPDATES
import com.plart.newsnow.core.utils.extentions.createNotificationChannel
import com.plart.newsnow.core.utils.extentions.getNotificationBuilder
import com.plart.newsnow.core.utils.extentions.logi

class TrackingService: Service() {
    private lateinit var client: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
//        startNotification()
        client = LocationServices.getFusedLocationProviderClient(this)
    }

//    private fun startNotification() {
//        val channelId = createNotificationChannel(
//            this,
//            NotificationManagerCompat.IMPORTANCE_HIGH,
//            true,
//            getString(R.string.app_name),
//            "tracking yeah!"
//        )
//        val notification = getNotificationBuilder(this, channelId).build()
//        startForeground(1, notification)
//    }

    // Initiate the request to track the device's location
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest().apply {
            interval = 5000
            //fastestInterval = 4000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            //smallestDisplacement = 500.toFloat() //if user traveled 500m
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestLocationUpdates();
        return super.onStartCommand(intent, flags, startId)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            val location = result?.lastLocation
            location?.let {
                val latLng = "${location.latitude},${location.longitude}"
                Log.d("mylog", "latitude: $location.latitude - longitud: $location.longitude")
//                logi("requestLocationUpdates: onLocationResult:: location = $latLng")
//                val intent = Intent(LOCATION_UPDATES)
//                intent.putExtra(LOCATION_EXTRA, latLng)
//                sendBroadcast(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}