package fr.isen.walkysocial

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.isen.walkysocial.Activities.Connexion
import fr.isen.walkysocial.Activities.History
import fr.isen.walkysocial.Models.User
import fr.isen.walkysocial.Services.GPSService


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMyLocationButtonClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Verification si l'utilisateur existe

        if (user.username.equals("")) {
            val con = Intent(applicationContext, Connexion::class.java)
            startActivity(con)
            finish()
        } else {
            // Foreground Service

            val intent = Intent(applicationContext, GPSService::class.java)
            applicationContext.startForegroundService(intent)

            // Maps

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapView) as SupportMapFragment?
            mapFragment!!.getMapAsync(this)

            // Boutons
            val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.item_1 -> {
                        // Respond to navigation item 1 click
                        true
                    }
                    R.id.item_2 -> {
                        val hist = Intent(applicationContext, History::class.java)
                        startActivity(hist)
                        finish()
                        true
                    }
                    else -> false
                }
            }
        }

    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private class SingBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            val action = intent.action //may need to chain this to a recognizing function
            Log.d("test", "Aller les bleus : $action")
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // Add the name and address to an array adapter to show in a Toast
                val derp = device!!.address
                Log.d("test", "test $derp")
            }
        }
    }

    private var map: GoogleMap? = null
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.setOnMyLocationButtonClickListener(this)
        map!!.setOnMyLocationClickListener(this)
        enableMyLocation()
        Log.d("Test", "La maps est chargé")

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        Log.d("test", "Last location")
        try {
            map!!.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location!!.latitude,
                        location.longitude
                    ), 15f
                )
            )
        } catch (e: Exception) {

        }


    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map!!.isMyLocationEnabled = true
            return
        }

        // 2. If if a permission rationale dialog should be shown
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        var user: User = User()
    }
}