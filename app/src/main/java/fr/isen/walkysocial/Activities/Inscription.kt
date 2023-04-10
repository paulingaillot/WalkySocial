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
import fr.isen.walkysocial.Models.UserClass
import fr.isen.walkysocial.R

class Inscription : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)


        var but = findViewById<Button>(R.id.button)

        but.setOnClickListener {

            var username = findViewById<EditText>(R.id.editTextTextPersonName)
            var radiogroup = findViewById<RadioGroup>(R.id.radiogroup)
            var password = findViewById<EditText>(R.id.password)

            if(radiogroup.checkedRadioButtonId != 0 && username.text.isNotEmpty()) {
                var radiobutcheck = findViewById<RadioButton>(radiogroup.checkedRadioButtonId)

                var userClass = UserClass.TANK;
                when(radiobutcheck.text.toString()) {
                    "Tank" ->
                        userClass = UserClass.TANK;
                    "Guerrier" ->
                        userClass = UserClass.GUERRIER
                    "Archer" ->
                        userClass = UserClass.ARCHER
                    "Mage" ->
                        userClass = UserClass.MAGE
                }

                var user = User(username.text.toString(), userClass , password.text.toString())

                val db = Firebase.firestore
                var profils = db.collection("user")

                profils.add(user).addOnCompleteListener {
                    MainActivity.user = user
                    val main = Intent(applicationContext, MainActivity::class.java)
                    startActivity(main)
                    finish()
                }



            }



        }
    }
}