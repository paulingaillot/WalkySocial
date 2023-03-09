package fr.isen.walkysocial.Activities

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fr.isen.walkysocial.MainActivity
import fr.isen.walkysocial.Models.User
import fr.isen.walkysocial.R

class Inscription : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)


        var but = findViewById<Button>(R.id.button)

        but.setOnClickListener {

            var username = findViewById<EditText>(R.id.editTextTextPersonName)
            var radiogroup = findViewById<RadioGroup>(R.id.radiogroup)

            if(radiogroup.checkedRadioButtonId != 0 && username.text.isNotEmpty()) {
                Log.d("button", radiogroup.checkedRadioButtonId.toString())
                Log.d("username", username.text.toString())
                var radiobutcheck = findViewById<RadioButton>(radiogroup.checkedRadioButtonId)
                Log.d("button", radiobutcheck.text.toString())

                // Recuperer Adresse mac de l'appareil en question

                var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val macAddress = bluetoothAdapter.address
                Log.d("MAC address", macAddress)

                var user = User(username.text.toString(), radiobutcheck.text.toString(), macAddress)

                val db = Firebase.firestore
                var profils = db.collection("user")

                profils.add(user)

                val main = Intent(applicationContext, MainActivity::class.java)
                startActivity(main)
                finish()

            }



        }
    }
}