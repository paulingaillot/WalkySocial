package fr.isen.walkysocial.Activities

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
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
                    var attack_view = findViewById<ConstraintLayout>(R.id.attack_view)
                    attack_view.visibility = View.VISIBLE

                    val scrollView = findViewById<ScrollView>(R.id.scrollView3)
                    var list_action = findViewById<LinearLayout>(R.id.list_action)
                    var win: Boolean = MainActivity.user.fight(boss){

                        var action  = TextView(this)
                        action.text = it
                        list_action.addView(action)
                        scrollView.post {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                        }
                    }
                    if(win) {
                        var action  = TextView(this)
                        action.text = "Vous avez gagné. Vous remportez 500 coins"
                        list_action.addView(action)
                        scrollView.post {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                        }

                        var but = findViewById<Button>(R.id.fin)
                        but.text = "Recuperer prix"
                        but.setOnClickListener {
                            MainActivity.user.coins += boss.max_HP
                            attack_view.visibility = View.GONE
                        }
                        MainActivity.bosses.remove(boss);
                        boss.remove()
                    }else {
                        var action  = TextView(this)
                        action.text = "Vous avez perdu. Reposez vous puis attaquez a nouveau."
                        list_action.addView(action)
                        scrollView.post {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                        }

                        var but = findViewById<Button>(R.id.fin)
                        but.text = "Continuer"
                        but.setOnClickListener {
                            attack_view.visibility = View.GONE
                        }
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
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Invite friends")

            // Créer un EditText et l'ajouter à la vue de la popup
            val textView = TextView(this)
            textView.setText(shortLink.toString())
            textView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textView.setOnClickListener {
                val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("label", textView.text)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            builder.setView(textView)

            builder.setPositiveButton("Share") { _, _ ->
                // Action à effectuer lorsque l'utilisateur clique sur Copy
                // Création de l'intent pour partager le texte
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())

                // Vérification si une application peut gérer l'intent de partage
                val packageManager = packageManager
                val activities: List<ResolveInfo> =
                    packageManager.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
                val isIntentSafe = activities.isNotEmpty()

                // Lancement de l'intent de partage si une application peut le gérer, sinon affichage d'un message
                if (isIntentSafe) {
                    startActivity(Intent.createChooser(shareIntent, "Share via"))
                } else {
                    Toast.makeText(this, "No sharing app is available", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton(
                "Copy to clipboard"
            ) { _, _ ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                // Copie du texte dans le presse-papier
                clipboard.setPrimaryClip(ClipData.newPlainText("Text copied", shortLink.toString()))

                Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}