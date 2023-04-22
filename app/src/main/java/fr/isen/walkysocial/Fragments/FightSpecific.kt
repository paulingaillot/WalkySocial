package fr.isen.walkysocial.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fr.isen.walkysocial.MainActivity
import fr.isen.walkysocial.Models.Boss
import fr.isen.walkysocial.R


class FightSpecific : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fight_specific, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id: String = arguments?.getString("id", "")!!

        val db = Firebase.firestore
        db.collection("boss").document(id).get().addOnCompleteListener {
            var boss: Boss = it.result.toObject(Boss::class.java)!!

            view.findViewById<TextView>(R.id.pv).setText("PV = ${boss.HP} / ${boss.max_HP}")
            var list_action = view.findViewById<LinearLayout>(R.id.list_action)
            var scrollView = view.findViewById<ScrollView>(R.id.scrollView3)

            view.findViewById<TextView>(R.id.button4).setOnClickListener {
                var win: Boolean = MainActivity.user.fight(boss){
                    var action  = TextView(context)
                    action.text = it
                    list_action.addView(action)
                    scrollView.post {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                }
                if(win) {
                    var action  = TextView(context)
                    action.text = "Vous avez gagné. Vous remportez 500 coins"
                    list_action.addView(action)
                    scrollView.post {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }

                    var but = view.findViewById<Button>(R.id.button4)
                    but.text = "Recuperer prix"
                    but.setOnClickListener {
                        MainActivity.user.coins += boss.max_HP
                        view.visibility = View.INVISIBLE;
                    }
                    MainActivity.bosses.remove(boss);
                    boss.remove()
                }else {
                    var action  = TextView(context)
                    action.text = "Vous avez perdu. Reposez vous puis attaquez a nouveau."
                    list_action.addView(action)
                    scrollView.post {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }

                    var but = view.findViewById<Button>(R.id.fin)
                    but.text = "Continuer"
                    but.setOnClickListener {
                        view.visibility = View.INVISIBLE;
                    }
                    boss.save()
                }
                MainActivity.user.save()

            }
        }

    }


}