package fr.isen.walkysocial.Services

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fr.isen.walkysocial.MainActivity
import fr.isen.walkysocial.Models.Position
import fr.isen.walkysocial.Models.Rencontre
import fr.isen.walkysocial.Models.User
import java.util.*


class GPSService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Test Fonctionnement", "Ca maarche")

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "channel_id",
            "My Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "channel_id")
            .setSmallIcon(R.drawable.ic_notification_overlay)
            .setContentTitle("WalkySocial ")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line...")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)

        val notification = builder.build()

        startForeground(1234, notification)

        Thread {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                @SuppressLint("MissingPermission")
                override fun run() {

                    val db = Firebase.firestore
                    var profils = db.collection("user")

                    val locationManager =
                        getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val location =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                    profils.get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            var docs = it.result.documents
                            for (doc in docs) {

                                Log.d("test", doc.get("username").toString())
                                var user1: User = doc.toObject(User::class.java)!!
                                if (MainActivity.user.username != user1.username) {
                                    var location_user1 = Location("").apply {
                                        Log.d("test", user1.username + " / " + user1.position.lat)
                                        latitude = user1.position.lat
                                        longitude = user1.position.long
                                    }
                                    var dist =10000.0;
                                        try {
                                            dist = location!!.distanceTo(location_user1).toDouble()
                                        } catch (e: Exception) {

                                        }
                                    if (dist < 50) {
                                        if (MainActivity.user.AvatarRencontre[user1.username] == null) {
                                            var recontre = Rencontre()
                                            recontre.nombre_rencontre = 1
                                            recontre.date_last_rencontre =
                                                System.currentTimeMillis()
                                            recontre.position =
                                                Position(location!!.latitude, location.longitude)
                                            MainActivity.user.AvatarRencontre[user1.username] =
                                                recontre

                                            MainActivity.user.save()
                                        } else {
                                            if (MainActivity.user.AvatarRencontre[user1.username]!!.date_last_rencontre < (System.currentTimeMillis() - 86400000)) {
                                                var recontre = Rencontre()
                                                recontre.nombre_rencontre =
                                                    MainActivity.user.AvatarRencontre[user1.username]!!.nombre_rencontre + 1
                                                recontre.date_last_rencontre =
                                                    System.currentTimeMillis()
                                                recontre.position =
                                                    Position(location!!.latitude, location.longitude)
                                                MainActivity.user.AvatarRencontre[user1.username] =
                                                    recontre

                                                MainActivity.user.save()
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }


                }
            }, 0, 60000)


            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    p0 ?: return
                    for (location in p0.locations) {

                        var prev_location = Location("").apply {
                            latitude = MainActivity.user.position.lat
                            longitude = MainActivity.user.position.long
                        }
                        if (prev_location.distanceTo(location) > 50.0) {
                            MainActivity.user.position =
                                Position(location.latitude, location.longitude)
                            MainActivity.user.save()
                        }

                        // Faire quelque chose avec la nouvelle localisation de l'utilisateur.


                    }
                }
            }

            val locationRequest: com.google.android.gms.location.LocationRequest =
                com.google.android.gms.location.LocationRequest.create().apply {
                    interval =
                        10000 // Temps entre les mises à jour de la localisation en millisecondes.
                    fastestInterval =
                        5000 // Temps le plus rapide entre les mises à jour de la localisation en millisecondes.
                    priority =
                        com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY // Précision de la localisation.
                }


            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

        }.start()

        // Ajoutez votre logique de service ici
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("test", "pitié")
        super.onDestroy()
        // Ajoutez votre logique de destruction de service ici
    }

}