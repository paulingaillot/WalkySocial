package fr.isen.walkysocial.Activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import fr.isen.walkysocial.MainActivity
import fr.isen.walkysocial.MainActivity.Companion.boost
import fr.isen.walkysocial.MainActivity.Companion.user
import fr.isen.walkysocial.Models.Objets
import fr.isen.walkysocial.R
import fr.isen.walkysocial.Services.GPSService
import java.lang.Integer.max

class Profil : AppCompatActivity() {

    val nomItems: HashMap<Objets, String> = hashMapOf(
        Objets.ATTACK to "attackPotion",
        Objets.DEFENSE to "defensePotion",
        Objets.PV to "PVPotion",
        Objets.DODGE to "dodgePotion"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val textDefense = findViewById<TextView>(R.id.textDefense)
        val textAttaque = findViewById<TextView>(R.id.textAttaque)
        val textType = findViewById<TextView>(R.id.textType)
        val textPV = findViewById<TextView>(R.id.textPV)
        val textDodge = findViewById<TextView>(R.id.textDodge)


        textDefense.text = getString(R.string.defense, user.Def.toString())
        textAttaque.text = getString(R.string.attaque, user.Atk.toString())
        textType.text = getString(R.string.type, user.classe.toString())
        textPV.text = getString(
            R.string.pv,
            user.HP.toString(),
            user.HP_Max.toString()
        )
        textDodge.text = getString(R.string.dodge, user.Esq.toString())

        for (objet in Objets.values()) {
            val resourceId = resources.getIdentifier(nomItems[objet] + "Boost", "id", packageName)
            val textBoost = findViewById<TextView>(resourceId)
            if (boost[objet] == true) {
                var boostValue = user.Atk
                when (objet) {
                    Objets.ATTACK -> {
                        boostValue = user.Atk
                        boostValue = max(boostValue + 1, (boostValue * 1.2).toInt())
                    }

                    Objets.DEFENSE -> {
                        boostValue = user.Def
                        boostValue = max(boostValue + 1, (boostValue * 1.2).toInt())
                    }

                    Objets.DODGE -> {
                        boostValue = user.Esq
                        boostValue = max(boostValue + 1, (boostValue * 1.2).toInt())
                    }

                    Objets.PV -> {
                        boostValue = user.HP_Max
                        boostValue = max(boostValue + 1, (boostValue * 1.2).toInt())
                    }
                }
                if(objet!=Objets.PV) {
                    textBoost.setText("Boost enabled : " + boostValue.toString())
                }else{
                    textBoost.setText("Boost enabled : " + user.HP.toString() + "/"+ boostValue.toString())
                }
            } else {
                textBoost.setText("")
            }
        }


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

                R.id.item_3 -> {
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