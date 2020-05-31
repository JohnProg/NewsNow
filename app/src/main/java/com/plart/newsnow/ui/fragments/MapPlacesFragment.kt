package com.plart.newsnow.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.plart.newsnow.core.LocationPermissionNotEnabledException
import com.plart.newsnow.core.LocationPermissionNotGrantedException
import com.plart.newsnow.core.asDeferred
import com.plart.newsnow.core.services.TrackingService
import com.plart.newsnow.core.utils.Constants.Companion.PERMISSION_REQUEST_LOCATION
import com.plart.newsnow.core.utils.extentions.openLocationSettings

import com.plart.newsnow.databinding.FragmentMapPlacesBinding
import com.plart.newsnow.models.Place
import com.plart.newsnow.models.Trip
import com.plart.newsnow.ui.NewsActivity
import kotlinx.android.synthetic.main.fragment_map_places.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MapPlacesFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fragmentMapPlacesFragment: FragmentMapPlacesBinding
    private lateinit var googleMap: GoogleMap
    lateinit var client: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val TAG = "MapPlacesFragment"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_view.onCreate(savedInstanceState)
        map_view.onResume()

        map_view.getMapAsync(this)

        client = LocationServices.getFusedLocationProviderClient(activity as NewsActivity)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val deviceLocation: Location? = getLastLocation().await()
                deviceLocation?.let {
                    val longitude = deviceLocation.longitude
                    val latitude = deviceLocation.latitude
                    val position = LatLng(latitude, longitude)
                    Toast.makeText(
                        activity,
                        "My Location: Latitude $latitude - Longitude $longitude",
                        Toast.LENGTH_LONG
                    ).show()
                    googleMap.addMarker(MarkerOptions().position(position).title("My Location"))
                    googleMap.isMyLocationEnabled = true
                }
            } catch (e: Exception) {
                when(e) {
                    is LocationPermissionNotEnabledException -> {
                        Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
                        (activity as NewsActivity).openLocationSettings()
                    }
                    is LocationPermissionNotGrantedException -> requestPermissions()
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            googleMap = it

            // Add a marker in Sydney and move the camera
            val sydney = LatLng(-34.0, 151.0)
            googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMapPlacesFragment = FragmentMapPlacesBinding.inflate(layoutInflater)
        return fragmentMapPlacesFragment.root
    }

    private fun startService() {
        val intent = Intent(activity, TrackingService::class.java)
        (activity as NewsActivity).startService(intent)
    }

    private fun getLastLocation(): Deferred<Location?> {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                this.startService()
                return client.lastLocation.asDeferred()
            } else {
                throw LocationPermissionNotEnabledException()
            }
        } else {
            throw LocationPermissionNotGrantedException()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                activity as NewsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            (activity as NewsActivity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            } else {
                Toast.makeText(
                    activity,
                    "Please, set location manually in settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
