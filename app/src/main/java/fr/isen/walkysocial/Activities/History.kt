package fr.isen.walkysocial.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.marginEnd
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.api.Distribution.BucketOptions.Linear
import fr.isen.walkysocial.MainActivity
import fr.isen.walkysocial.Models.Rencontre
import fr.isen.walkysocial.Models.User
import fr.isen.walkysocial.R
import org.w3c.dom.Text
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.HashMap

class History : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val list = findViewById<LinearLayout>(R.id.list)

        // Affichage Historique


        var u: User = MainActivity.user
        var history: HashMap<String, Rencontre> = u.AvatarRencontre

        for (r in history) {
            var username: String = r.key
            var rencontre: Int = r.value.nombre_rencontre
            val instant = Instant.ofEpochMilli(r.value.date_last_rencontre)
            val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            var dateEncoded: String =
                date.dayOfMonth.toString() + "/" + date.month.toString() + "/" + date.year.toString() + " " + date.hour.toString() + ":" + date.minute.toString() + ":" + date.second.toString()

            // Affichage

            var l2 = LinearLayout(this)
            l2.orientation = LinearLayout.HORIZONTAL

            var usernameEntry = TextView(this)
            User.getUserByUid(username){usernameEntry.text = it.username}
            usernameEntry.setPadding(100, 0, 100, 0)
            l2.addView(usernameEntry)

            var l3 = LinearLayout(this)
            l3.orientation = LinearLayout.VERTICAL

            var l4 = LinearLayout(this)
            l4.orientation = LinearLayout.HORIZONTAL

            var progress = LinearProgressIndicator(this)
            progress.setProgressCompat(progressToNextFibonacci(rencontre).toInt(), true)
            progress.setPadding(0, 0, 100, 0)

            var progressValue = TextView(this)
            progressValue.text = "$rencontre / ${nextFibonacci(rencontre)}"


            l4.addView(progressValue)
            l4.addView(progress)


            var lastSeen = TextView(this)
            lastSeen.text = "Dernière rencontre : " + dateEncoded

            l3.addView(l4)
            l3.addView(lastSeen)

            l2.addView(l3)
            list.addView(l2)

        }

        // Boutons

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.item_4
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_3 -> {
                    val main = Intent(applicationContext, MainActivity::class.java)
                    finish()
                    true
                }

                R.id.item_4 -> {

                    true
                }

                else -> false
            }
        }
    }

    fun progressToNextFibonacci(n: Int): Float {
        // Vérifier si l'entrée est valide
        if (n <= 0) {
            throw IllegalArgumentException("L'entrée doit être un entier positif.")
        }

        // Calculer les deux nombres de Fibonacci les plus proches de l'entrée
        var a = 0
        var b = 1
        while (b <= n) {
            val temp = a
            a = b
            b = temp + b
        }

        val progressToNext = (n.toFloat() / b.toFloat()) * 100

        return progressToNext
    }

    fun nextFibonacci(n: Int): Int {
        // Vérifier si l'entrée est valide
        if (n < 0) {
            throw IllegalArgumentException("L'entrée doit être un entier positif ou nul.")
        }

        // Cas de base pour 0 et 1
        if (n == 0 || n == 1) {
            return 1
        }

        // Initialiser les deux nombres de Fibonacci précédents
        var a = 0
        var b = 1

        // Itérer jusqu'à trouver le prochain nombre de Fibonacci
        while (true) {
            val temp = a
            a = b
            b = temp + b

            if (b > n) {
                return b
            }
        }
    }
}