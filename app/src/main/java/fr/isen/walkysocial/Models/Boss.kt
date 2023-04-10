package fr.isen.walkysocial.Models

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Boss {

    var id:String = ""

    var HP: Int = 0
    var max_HP: Int = 0
    var def:Int = 1
    var atk: Int = 1

    var position: Position = Position()

    fun save() {
        val db = Firebase.firestore
        val profils = db.collection("boss")

        profils.document(id).set(this)
    }

    fun remove() {
        val db = Firebase.firestore
        val profils = db.collection("boss")

        profils.document(id).delete()
    }


}