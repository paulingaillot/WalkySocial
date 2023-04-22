package fr.isen.walkysocial.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
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

            var email = usernameView.text.toString();
            var password = passwordView.text.toString();

            var auth = Firebase.auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("test", "signInWithEmail:success")
                        User.getUserByUid(task.result.user!!.uid){
                            if(it.uid == task.result.user!!.uid) {
                                MainActivity.user = it

                                val main = Intent(applicationContext, MainActivity::class.java)
                                startActivity(main)
                                finish()
                            }
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("test", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
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