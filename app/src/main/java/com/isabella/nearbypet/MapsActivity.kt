package com.isabella.nearbypet

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.io.IOException


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATED_IDENTITY_EQUALS")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var searchView: SearchView
    private lateinit var client: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        client = LocationServices.getFusedLocationProviderClient(this)

        searchView = findViewById(R.id.search_view_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // adding on query listener for our search view.
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // on below line we are getting the
                // location name from search view.
                val location = searchView!!.query.toString()

                // below line is to create a list of address
                // where we will store the list of all address.
                var addressList: List<Address>? = null

                // checking if the entered location is null or not.
                if (location != null || location == "") {
                    // on below line we are creating and initializing a geo coder.
                    val geocoder = Geocoder(this@MapsActivity)
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 5)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    val address: Address = addressList!![0]

                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    val latLng = LatLng(address.getLatitude(), address.getLongitude())

                    // on below line we are adding marker to that position.
                    mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
                        .setIcon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_VIOLET))

                    // below line is to animate camera to that position.
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        // at last we calling our map fragment to update.
        mapFragment.getMapAsync(this)

        checkPermission()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
//    Capture last location of device
        client.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap!!.addMarker(MarkerOptions().position(latLng).title(location.toString()))
                        .setIcon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_VIOLET))
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                } else {
                    Log.i("Teste",
                        "null")
                }
            }
            .addOnFailureListener { }

//      Setting the interval that my request will capture the location of the user
//      Setting the locations of the others apps to my app consume (nearby locations)
//      Setting the precision of my location and putting the economy of my battery.
        val locationRequest : LocationRequest = LocationRequest.create()
        locationRequest.interval = 15 * 1000
        locationRequest.fastestInterval = 5 * 1000
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        val builder : LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
//      Checking if my configs is correct
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener { locationResponse ->
                Log.i("Teste",
                    locationResponse.locationSettingsStates.isNetworkLocationPresent.toString())
            }
            .addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        val resolvable: ResolvableApiException = e
                        resolvable.startResolutionForResult(this@MapsActivity, 10)
                    } catch (e1: IntentSender.SendIntentException) {
                    }
                }
            }

        val locationCallback: LocationCallback = object : LocationCallback() {
//          Capture current location after 15sec
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                    if (locationResult == null) {
                        Log.i("Teste2", "Local is null")
                        return
                    }
                    for (location: Location in locationResult.locations) {
                        Log.i("Teste2", location.latitude.toString())
                    }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                Log.i("Teste", locationAvailability.isLocationAvailable.toString())
            }
        }

        client.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            checkPermission()
            return
        }

        val latLng2 = LatLng(-23.6931778, -46.616407)
        val latLng3 = LatLng(-23.6927823,-46.617248)
        mMap!!.addMarker(MarkerOptions().position(latLng2).title("Outro Abrigo aqui"))
            .setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_VIOLET))

        mMap!!.addMarker(MarkerOptions().position(latLng3).snippet("Teste").title("Teste abrigo"))
            .setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_VIOLET))


        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this@MapsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@MapsActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this@MapsActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MapsActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                            PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(
                            this,
                            "Permissão de localização concedida",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this,
                        "Permissão negada. Para utilizar o aplicativo, " +
                            "permita o acesso a localização",
                        Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

}










