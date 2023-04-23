package fr.isen.walkysocial.Activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import fr.isen.walkysocial.MainActivity
import fr.isen.walkysocial.MainActivity.Companion.user
import fr.isen.walkysocial.R
import fr.isen.walkysocial.Services.GPSService

class Profil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val textDefense = findViewById<TextView>(R.id.textDefense)
        val textAttaque = findViewById<TextView>(R.id.textAttaque)
        val textType = findViewById<TextView>(R.id.textType)
        val textPV = findViewById<TextView>(R.id.textPV)
        val textDodge = findViewById<TextView>(R.id.textDodge)

        textDefense.text = getString(R.string.defense, MainActivity.user.Def.toString())
        textAttaque.text = getString(R.string.attaque, MainActivity.user.Atk.toString())
        textType.text = getString(R.string.type, MainActivity.user.classe.toString())
        textPV.text = getString(R.string.pv, MainActivity.user.HP.toString(), MainActivity.user.HP_Max.toString())
        textDodge.text = getString(R.string.dodge, MainActivity.user.Esq.toString())


        // Boutons
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    val fight = Intent(applicationContext, Fight::class.java)
                    startActivity(fight)
                    finish()
                    true
                }
                R.id.item_2 -> {
                    val shop = Intent(applicationContext, Shop::class.java)
                    startActivity(shop)
                    finish()
                    true
                }
                R.id.item_3-> {
                    val main = Intent(applicationContext, MainActivity::class.java)
                    finish()
                    // Respond to navigation item 1 click
                    true
                }
                R.id.item_4 -> {
                    val hist = Intent(applicationContext, History::class.java)
                    startActivity(hist)
                    finish()
                    true
                }
                else -> false
            }
        }



        val butChangerNom = findViewById<Button>(R.id.changerNom)
        val butDeco = findViewById<Button>(R.id.buttonDeco)

        butChangerNom.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.change_username))

            // Créer un EditText et l'ajouter à la vue de la popup
            val editText = EditText(this)
            editText.hint = getString(R.string.write_username)
            builder.setView(editText)

            builder.setPositiveButton(getString(R.string.sauvegarder)) { _, _ ->
                // Action à effectuer lorsque l'utilisateur clique sur Sauvegarder
                if (editText.text.toString() != "") {
                    val username = editText.text.toString()
                    user.username = username
                    user.save()
                }
            }
            builder.setNegativeButton(
                getString(R.string.annuler)
            ) { _, _ ->
            }
            val dialog = builder.create()
            dialog.show()
        }


        butDeco.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //deconnecter l'utilisateur
            val intentStop = Intent(this, GPSService::class.java)
            intentStop.action = "ACTION_STOP_SERVICE"
            sendBroadcast(intentStop)
            stopService(intentStop)
            val connexion = Intent(applicationContext, Connexion::class.java)
            startActivity(connexion)
            finish()
        }

    }
}