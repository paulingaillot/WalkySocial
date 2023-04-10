package fr.isen.walkysocial.Activities

import android.content.Intent
import android.graphics.drawable.GradientDrawable.Orientation
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import fr.isen.walkysocial.Fragments.FightSpecific
import fr.isen.walkysocial.MainActivity
import fr.isen.walkysocial.R

class Fight : AppCompatActivity() {

    var myFragment: FightSpecific = FightSpecific()

    override fun onResume() {
        super.onResume()
        loadBosses()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fight)


        if(intent.hasExtra("BossId")) {
            var BossId = intent.getStringExtra("BossId");

            // Créer un Bundle pour passer des données
            val bundle = Bundle()
            bundle.putString("id", BossId)
            myFragment.arguments = bundle

            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fightSpecific, myFragment)
            fragmentTransaction.commit()
            val test = findViewById<FragmentContainerView>(R.id.fightSpecific)
            test.visibility = View.VISIBLE;
        }

        loadBosses()
        // Boutons
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.item_1
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    true
                }
                R.id.item_2 -> {
                    true
                }
                R.id.item_3-> {
                    val main = Intent(applicationContext, MainActivity::class.java)
                    startActivity(main)
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

    }

    fun loadBosses() {
        var fi = findViewById<LinearLayout>(R.id.list)
        fi.removeAllViews()

        var i =1;
        MainActivity.bosses.forEach {boss->

            var li = LinearLayout(this)
            li.orientation = LinearLayout.HORIZONTAL
            var nom = TextView(this)
            nom.setText("Boss n°${i}")
            nom.setPadding(20,20,20,20)
            var PV = TextView(this)
            PV.setText("${boss.HP} / ${boss.max_HP}")
            PV.setPadding(20,20,20,20)
            var buttonInvite= Button(this)
            buttonInvite.setText("Invite Friends")
            buttonInvite.setOnClickListener {
                createDynamicLink(boss.id);
            }
            var button = Button(this)
            button.setText("Fight")
            button.setPadding(20,20,20,20)
            button.setOnClickListener {
                if(MainActivity.user.HP == 0) {
                    Toast.makeText(this, "Désolé mais vous ne pouvez pas attaquer de boss si vous n'avez pas de vie", Toast.LENGTH_SHORT).show()
                } else {
                    var win: Boolean = MainActivity.user.fight(boss)
                    if(win) {
                        MainActivity.bosses.remove(boss);
                        boss.remove()
                    }else {
                        boss.save()
                    }
                    MainActivity.user.save()
                    loadBosses()
                }
            }
            i++;

            li.addView(nom)
            li.addView(PV)
            li.addView(buttonInvite)
            li.addView(button)


            fi.addView(li)
        }
    }

    // Créer le lien dynamique
    fun createDynamicLink(id:String) {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://example.com/deep_link")
                .buildUpon()
                .appendQueryParameter("BossId", id)
                .build())
            .setDomainUriPrefix("https://walkysocial.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("fr.isen.walkysocial")
                    .setMinimumVersion(1)
                    .build()
            )
            .buildDynamicLink()

        // Raccourcir le lien dynamique
        FirebaseDynamicLinks.getInstance().shortLinkAsync {
            setLongLink(dynamicLink.uri)
            setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle("Title")
                    .setDescription("Description")
                    .build()
            )
            buildShortDynamicLink()
        }.addOnCompleteListener {
            // Lien dynamique raccourci
            val shortLink = it.result.shortLink
            Log.d("test", shortLink.toString())
            // Utilisez le lien dynamique raccourci comme nécessaire
        }
    }
}