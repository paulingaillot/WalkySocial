package fr.isen.walkysocial

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fr.isen.walkysocial.Activities.Connexion
import fr.isen.walkysocial.Activities.Fight
import fr.isen.walkysocial.Activities.History
import fr.isen.walkysocial.Activities.Profil
import fr.isen.walkysocial.Activities.Shop
import fr.isen.walkysocial.Models.Boss
import fr.isen.walkysocial.Models.Objets
import fr.isen.walkysocial.Models.User
import fr.isen.walkysocial.Services.GPSService
import java.lang.Integer.min
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.thread
import kotlin.concurrent.timerTask


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMyLocationButtonClickListener {

    var maxAtk: Int = 5 * 60
    var maxDef: Int = 5 * 60
    var maxPV: Int = 5 * 60
    var maxDodge: Int = 5 * 60
    lateinit var testatk : LinearLayout
    lateinit var testDef : LinearLayout
    lateinit var testDodge : LinearLayout
    lateinit var testPV : LinearLayout

    override fun onStart() {
        super.onStart()

        // Écouter les Intent d'ouverture de l'application
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                // Récupérer le lien dynamique s'il existe
                val deepLink: Uri? = pendingDynamicLinkData?.link

                // Vérifier si le lien dynamique existe
                if (deepLink != null) {
                    // Récupérer les données du lien dynamique si nécessaire
                    val id = deepLink.getQueryParameter("BossId")

                    // Rediriger vers l'activité spécifique en fonction du lien dynamique
                    if (deepLink.path == "/deep_link") {
                        // Rediriger vers l'activité spécifique de votre application
                        val intent = Intent(this, Fight::class.java)
                        // Passer les données du lien dynamique si nécessaire
                        intent.putExtra("BossId", id)
                        startActivity(intent)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Gestion des erreurs
            }
    }

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

            // Boutons
            val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigation.selectedItemId = R.id.item_3
            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.item_1 -> {
                        val fight = Intent(applicationContext, Fight::class.java)
                        startActivity(fight)
                        true
                    }

                    R.id.item_2 -> {
                        val shop = Intent(this, Shop::class.java)
                        startActivity(shop)
                        true
                    }

                    R.id.item_3 -> {
                        // Respond to navigation item 1 click
                        true
                    }

                    R.id.item_4 -> {
                        val hist = Intent(applicationContext, History::class.java)
                        startActivity(hist)
                        true
                    }

                    else -> false
                }
            }

            val buttonProfil = findViewById<FloatingActionButton>(R.id.buttonProfil)
            buttonProfil.setOnClickListener {
                val intent = Intent(applicationContext, Profil::class.java)
                startActivity(intent)
            }

            // getBooses
            val db = Firebase.firestore
            var profils = db.collection("boss")

            bosses = ArrayList()
            profils.get().addOnCompleteListener {
                it.result.forEach { boss ->
                    bosses.add(boss.toObject(Boss::class.java))
                }
            }

            boost = hashMapOf(
                Objets.ATTACK to false,
                Objets.DEFENSE to false,
                Objets.PV to false,
                Objets.DODGE to false
            )

            //Update HP User

            val timer = Timer()
            timer.scheduleAtFixedRate(timerTask {
                user.updateHP()
                runOnUiThread {
                    findViewById<LinearProgressIndicator>(R.id.progress).progress =
                        user.getLifePercent().toInt()
                }
                user.save()
            },0, 60 * 1000)

            // Maps

            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapView) as SupportMapFragment?
            mapFragment!!.getMapAsync(this)


            //Affichage HP user
            user.HP = min(user.HP, user.HP_Max)
            findViewById<LinearProgressIndicator>(R.id.progress).progress =
                user.getLifePercent().toInt()
        }

        // Creation Du BroadCast Receiver

        val filter = IntentFilter()
        filter.addAction("com.example.MY_ACTION2")
        registerReceiver(myReceiver, filter)

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

        // Chargement des points autours
        val db = Firebase.firestore
        val boss = db.collection("boss")
        boss.get().addOnCompleteListener { it ->
            for (doc in it.result.documents) {
                var boss: Boss = doc.toObject(Boss::class.java)!!
                map!!.addMarker(
                    MarkerOptions()
                        .flat(false)
                        .icon(
                            bitmapDescriptorFromVector(
                                this,
                                R.drawable.radio_button_unchecked,
                                R.color.green
                            )
                        )
                        .position(LatLng(boss.position.lat, boss.position.long))
                        .title("Boss")
                        .snippet("${boss.HP} / ${boss.max_HP}")
                )
            }
        }

    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        vectorResId: Int,
        color: Int
    ): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
        //val width = (context.resources.displayMetrics.widthPixels * 0.05).toInt()
        //val height = width
        vectorDrawable?.setBounds(0, 0, 30, 30)
        vectorDrawable?.alpha = 150
        val colorFilter: ColorFilter =
            PorterDuffColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_IN)
        vectorDrawable?.colorFilter = colorFilter
        val bitmap = Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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

    private val myReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
                findViewById<LinearProgressIndicator>(R.id.progress).progress = 90
                if (intent?.action == "com.example.MY_ACTION2") {
                    val item = intent.getSerializableExtra("objet") as? Objets
                    if (item != null) {
                        if (boost[item] == true) {
                            when (item) {
                                Objets.ATTACK -> {
                                    maxAtk += 60
                                }

                                Objets.DEFENSE -> {
                                    maxDef += 5 * 60
                                }

                                Objets.DODGE -> {
                                    maxDodge += 5 * 60
                                }

                                Objets.PV -> {
                                    maxPV += 5 * 60
                                }
                                // autres cas
                                else -> {
                                    // code à exécuter pour les autres objets
                                }
                            }
                        } else {
                            if (item == Objets.PV) {
                                user.HP = Integer.max(user.HP + 1, (user.HP * 1.2).toInt())
                            }

                            /*val resourceId = resources.getIdentifier("iconButton" + nomItems[item], "id", packageName)
                        Log.d("test", "iconButton : " +"iconButton" + nomItems[item])
                        val test = findViewById<LinearLayout>(resourceId)
                        val resourceId2 = resources.getIdentifier("timer" + nomItems[item], "id", packageName)
                        val timer1 = findViewById<TextView>(resourceId2)
                        test.visibility = View.VISIBLE*/

                            boost[item] = true

                            var testatk = findViewById<LinearLayout>(R.id.iconButtonattackPotion)
                            var testDef = findViewById<LinearLayout>(R.id.iconButtondefensePotion)
                            var testDodge = findViewById<LinearLayout>(R.id.iconButtondodgePotion)
                            var testPV = findViewById<LinearLayout>(R.id.iconButtonPVPotion)

                            when (item) {
                                Objets.ATTACK -> {
                                    maxAtk = 5
                                    var timer1 = findViewById<TextView>(R.id.timerattackPotion)
                                    testatk.visibility = View.VISIBLE

                                    val timer = Timer()
                                    timer.schedule(object : TimerTask() {
                                        var i: Int = 0
                                        override fun run() {
                                            i++
                                            if (i == maxAtk) {
                                                runOnUiThread {
                                                    testatk.visibility = View.INVISIBLE
                                                }
                                                boost[item] = false
                                                this.cancel()
                                            }
                                            runOnUiThread {
                                                timer1.text = getString(R.string.sec, (maxAtk - i).toString())
                                            }
                                        }
                                    }, 0, 1000)
                                }

                                Objets.DEFENSE -> {
                                    maxDef = 5 * 60
                                    var timer1 = findViewById<TextView>(R.id.timerdefensePotion)
                                    testDef.visibility = View.VISIBLE
                                    val timer = Timer()
                                    timer.schedule(object : TimerTask() {
                                        var i: Int = 0
                                        override fun run() {
                                            i++
                                            if (i == maxDef) {
                                                runOnUiThread {
                                                    testDef.visibility = View.INVISIBLE
                                                }
                                                boost[item] = false
                                                this.cancel()
                                            }
                                            runOnUiThread {
                                                timer1.text =
                                                    getString(R.string.sec, (maxDef - i).toString())
                                            }
                                        }
                                    }, 0, 1000)
                                }

                                Objets.DODGE -> {
                                    maxDodge = 5 * 60
                                    var timer1 = findViewById<TextView>(R.id.timerdodgePotion)
                                    testDodge.visibility = View.VISIBLE
                                    val timer = Timer()
                                    timer.schedule(object : TimerTask() {
                                        var i: Int = 0
                                        override fun run() {
                                            i++
                                            if (i == maxDodge) {
                                                runOnUiThread {
                                                    testDodge.visibility = View.INVISIBLE
                                                }
                                                boost[item] = false
                                                this.cancel()
                                            }
                                            runOnUiThread {
                                                timer1.text = getString(
                                                    R.string.sec,
                                                    (maxDodge - i).toString()
                                                )
                                            }
                                        }
                                    }, 0, 1000)
                                }

                                Objets.PV -> {
                                    maxPV = 5 * 60
                                    var timer1 = findViewById<TextView>(R.id.timerPVPotion)
                                    val timer = Timer()
                                    testPV.visibility = View.VISIBLE
                                    timer.schedule(object : TimerTask() {
                                        var i: Int = 0
                                        override fun run() {
                                            i++
                                            if (i == maxPV) {
                                                runOnUiThread {
                                                    testPV.visibility = View.INVISIBLE
                                                }
                                                boost[item] = false
                                                user.HP = min(user.HP, user.HP_Max)
                                                this.cancel()
                                            }
                                            runOnUiThread {
                                                timer1.text =
                                                    getString(R.string.sec, (maxPV - i).toString())
                                            }
                                        }
                                    }, 0, 1000)
                                }
                            }


                            Log.d("test", "On a booste votre :$item")
                        }
                    }
                }
        }
    }


    override fun onPause() {
        user.save();
        super.onPause();
    }

    override fun onDestroy() {
        unregisterReceiver(myReceiver)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        findViewById<LinearProgressIndicator>(R.id.progress).progress =
            user.getLifePercent().toInt()
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.item_3
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        var user: User = User()
        var bosses: ArrayList<Boss> = ArrayList()
        var boost: HashMap<Objets, Boolean> = hashMapOf(
            Objets.ATTACK to false,
            Objets.DEFENSE to false,
            Objets.PV to false,
            Objets.DODGE to false
        )
    }
}