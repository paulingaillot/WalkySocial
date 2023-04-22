package fr.isen.walkysocial.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            view.findViewById<TextView>(R.id.button4).setOnClickListener {
                var win: Boolean = MainActivity.user.fight(boss){

                }
                if (win) {
                    MainActivity.bosses.remove(boss);
                    boss.remove()
                } else {
                    boss.save()
                }
                MainActivity.user.save()
                view.visibility = View.INVISIBLE;
            }
        }

    }


}