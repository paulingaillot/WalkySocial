package fr.isen.walkysocial.Activities

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fr.isen.walkysocial.MainActivity
import fr.isen.walkysocial.Models.User
import fr.isen.walkysocial.Models.UserClass
import fr.isen.walkysocial.R

class Connexion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connexion)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1234
        )

        var but = findViewById<Button>(R.id.button2)
        var but2 = findViewById<Button>(R.id.button3)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            User.getUserByUid(user.uid) {
                MainActivity.user = it
                val main = Intent(applicationContext, MainActivity::class.java)
                startActivity(main)
                finish()
            }
        } else {
            findViewById<ConstraintLayout>(R.id.loading).visibility = View.GONE
        }

        findViewById<Button>(R.id.google).setOnClickListener {
            var googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .build()
            val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }

        but.setOnClickListener {

            var usernameView = findViewById<EditText>(R.id.editTextTextPersonName)
            var passwordView = findViewById<EditText>(R.id.password)

            var email = usernameView.text.toString();
            var password = passwordView.text.toString();

            if (email != "" && password != "") {
                var auth = Firebase.auth
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("test", "signInWithEmail:success")
                            User.getUserByUid(task.result.user!!.uid) {
                                if (it.uid == task.result.user!!.uid) {
                                    MainActivity.user = it

                                    val main = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(main)
                                    finish()
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("test", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        but2.setOnClickListener {
            val insc = Intent(applicationContext, Inscription::class.java)
            startActivity(insc)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful && task.isComplete) {
                firebaseAuthWithGoogle(task.result.idToken)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        Log.d("test", "$idToken")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // L'utilisateur est authentifié avec Firebase
                    val user = FirebaseAuth.getInstance().currentUser

                    User.getUserByUid(user!!.uid) {
                        MainActivity.user = it
                        if (it.uid != user.uid) {
                            findViewById<ConstraintLayout>(R.id.inscgoogle).visibility =
                                View.VISIBLE
                            var but = findViewById<Button>(R.id.button5)

                            but.setOnClickListener {

                                var username = findViewById<EditText>(R.id.username)
                                var radiogroup = findViewById<RadioGroup>(R.id.radiogroup)

                                if (radiogroup.checkedRadioButtonId != 0 && username.text.isNotEmpty()) {
                                    var radiobutcheck =
                                        findViewById<RadioButton>(radiogroup.checkedRadioButtonId)

                                    var userClass = UserClass.TANK;
                                    when (radiobutcheck.text.toString()) {
                                        "Tank" ->
                                            userClass = UserClass.TANK;
                                        "Guerrier" ->
                                            userClass = UserClass.GUERRIER

                                        "Archer" ->
                                            userClass = UserClass.ARCHER

                                        "Mage" ->
                                            userClass = UserClass.MAGE
                                    }

                                    var user1 = User(
                                        username.text.toString(),
                                        userClass,
                                        user.uid
                                    )

                                    val db = Firebase.firestore
                                    var profils = db.collection("user")

                                    profils.add(user1).addOnCompleteListener {
                                        MainActivity.user = user1
                                        val main =
                                            Intent(applicationContext, MainActivity::class.java)
                                        startActivity(main)
                                        finish()
                                    }
                                }

                            }
                        } else {
                            Log.d("test", "Firebase auth success: ${user.uid}")
                            val main = Intent(applicationContext, MainActivity::class.java)
                            startActivity(main)
                            finish()
                        }

                    }

                } else {
                    Log.w("test", "Firebase auth failed", task.exception)
                }
            }
    }

    companion object {
        private val RC_SIGN_IN = 123
    }
}