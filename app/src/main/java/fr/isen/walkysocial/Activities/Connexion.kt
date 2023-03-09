package fr.isen.walkysocial.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fr.isen.walkysocial.MainActivity
import fr.isen.walkysocial.Models.User
import fr.isen.walkysocial.R

class Connexion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connexion)

        var but = findViewById<Button>(R.id.button2)
        var but2 = findViewById<Button>(R.id.button3)


        but.setOnClickListener {

            var usernameView = findViewById<EditText>(R.id.editTextTextPersonName)
            var passwordView = findViewById<EditText>(R.id.password)

            var username = usernameView.text.toString();
            var password = passwordView.text.toString();


            val db = Firebase.firestore
            val profils = db.collection("user")

            profils.whereEqualTo("username", username).whereEqualTo("password", password).get().addOnCompleteListener {
                if (it.result.size() == 0) {

                } else {
                    MainActivity.user = it.result.documents[0].toObject(User::class.java)!!

                    val main = Intent(applicationContext, MainActivity::class.java)
                    startActivity(main)
                    finish()
                }
            }


        }

        but2.setOnClickListener {
            val insc = Intent(applicationContext, Inscription::class.java)
            startActivity(insc)
            finish()
        }
    }
}