package fr.isen.walkysocial.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.isen.walkysocial.MainActivity
import fr.isen.walkysocial.Models.Objets
import fr.isen.walkysocial.R

class Shop : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        val prixItems: HashMap<Objets, Int> = hashMapOf(
            Objets.ATTACK to 100,
            Objets.DEFENSE to 100,
            Objets.PV to 100,
            Objets.DODGE to 100
        )
        val nomItems: HashMap<Objets, String> = hashMapOf(
            Objets.ATTACK to "attackPotion",
            Objets.DEFENSE to "defensePotion",
            Objets.PV to "PVPotion",
            Objets.DODGE to "dodgePotion"
        )

        //Initialisation des prix des items
        for (objet in Objets.values()) {
            val resourceId = resources.getIdentifier("prix_" + nomItems[objet], "id", packageName)
            findViewById<TextView>(resourceId).text = prixItems[objet].toString()
        }

        //Initialisation des boutons pour ajouter et enlever
        for (objet in Objets.values()) {
            //bouton ajout
            val resourceId =
                resources.getIdentifier(nomItems[objet] + "_buttonTopBuy", "id", packageName)
            val btnAjout = findViewById<Button>(resourceId)

            //bouton enlever
            val resourceId2 =
                resources.getIdentifier(nomItems[objet] + "_buttonBottomBuy", "id", packageName)
            val btnEnlever = findViewById<Button>(resourceId2)

            //text nombre à acheter
            val resourceId3 =
                resources.getIdentifier(nomItems[objet] + "_nbBuy", "id", packageName)
            val textAchat= findViewById<TextView>(resourceId3)

            //bouton acheter
            val resourceId4 =
                resources.getIdentifier(nomItems[objet] + "_buttonBuy", "id", packageName)
            val btnBuy = findViewById<Button>(resourceId4)

            //text stock utilisateur
            val resourceId5 =
                resources.getIdentifier(nomItems[objet] + "_nbUse", "id", packageName)
            val textStock= findViewById<TextView>(resourceId5)

            //Ajout des listeners aux boutons
            btnAjout.setOnClickListener {
                updateCounter(
                    textAchat,
                    1
                )
            }
            btnEnlever.setOnClickListener {
                updateCounter(
                    textAchat,
                    -1
                )
            }
            btnBuy.setOnClickListener {
                nomItems[objet]?.let { it1 -> buy(it1,textAchat,textStock,prixItems[objet]) }
            }
        }


        /*

                //Button acheter
                val btnBuyAttack = findViewById<Button>(R.id.attackPotion_buttonBuy)
                val btnBuyDefense = findViewById<Button>(R.id.defensePotion_buttonBuy)
                val btnBuyPV = findViewById<Button>(R.id.PVPotion_buttonBuy)
                val btnBuyDodge = findViewById<Button>(R.id.dodgePotion_buttonBuy)

                //Button utiliser objet
                val btnUseAttack = findViewById<Button>(R.id.attackPotion_buttonUse)
                val btnUseDefense = findViewById<Button>(R.id.defensePotion_buttonUse)
                val btnUsePV = findViewById<Button>(R.id.PVPotion_buttonUse)
                val btnUseDodge = findViewById<Button>(R.id.dodgePotion_buttonUse)

                //textView stock objet
                val nbUseAttack = findViewById<TextView>(R.id.attackPotion_nbUse)
                val nbUseDefense = findViewById<TextView>(R.id.defensePotion_nbUse)
                val nbUsePV = findViewById<TextView>(R.id.PVPotion_nbUse)
                val nbUseDodge = findViewById<TextView>(R.id.dodgePotion_nbUse)

                //Aout des listeners sur les boutons
                //ajout
                updateCounter(btnAjoutAttack, nbBuyAttack, 1)
                updateCounter(btnAjoutDefense, nbBuyDefense, 1)
                updateCounter(btnAjoutPV, nbBuyPV, 1)
                updateCounter(btnAjoutDodge, nbBuyDodge, 1)
                //enlever
                updateCounter(btnEnleverAttack, nbBuyAttack, -1)
                updateCounter(btnEnleverDefense, nbBuyDefense, -1)
                updateCounter(btnEnleverPV, nbBuyPV, -1)
                updateCounter(btnEnleverDodge, nbBuyDodge, -1)
        */


        // Barre de navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.item_2
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    val fight = Intent(applicationContext, Fight::class.java)
                    startActivity(fight)
                    finish()
                    true
                }

                R.id.item_2 -> {
                    true
                }

                R.id.item_3 -> {
                    val main = Intent(applicationContext, MainActivity::class.java)
                    startActivity(main)
                    finish()
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
    }

    fun updateCounter(textView: TextView, value: Int) {
        var count = textView.text.toString().toInt()
        count += value
        if(count>0) {
            textView.text = count.toString()
        }
    }

    fun buy(item:String, textNbBuy: TextView, textNbUse: TextView, prixItem : Int?) {
        /*
        Achète le nombre choisi (textNbBuy) de l'item correspondant.
        Selon le prix à l'unité de l'item, vérifie que user possède suffisamment d'argent pour l'achat
        */
        val nbBuy = textNbBuy.text.toString().toInt()
        var nbStock = MainActivity.user.stockItems[item]
        val coutTotal = nbBuy * prixItem!!

        if (MainActivity.user.coins >= coutTotal) {
            nbStock = nbStock!! + nbBuy
            MainActivity.user.stockItems[item] = nbStock
            MainActivity.user.coins -= coutTotal
            findViewById<TextView>(R.id.nbCoin).text = MainActivity.user.coins.toString()
            MainActivity.user.save()

            textNbUse.text = MainActivity.user.stockItems[item].toString()
            textNbBuy.text = "1"
        }
        else{

            Toast.makeText(this, getString(R.string.message_manqueArgent), Toast.LENGTH_SHORT).show()
        }
    }
}