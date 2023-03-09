package fr.isen.walkysocial.Models

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fr.isen.walkysocial.MainActivity

class User {

    var username: String = ""
    var password: String = ""

    var position: Position = Position(0.0,0.0)
    var classe: String = ""
    var AvatarRencontre: HashMap<String, Rencontre> = HashMap()

    constructor(username: String, classe: String, password: String) {
        this.username = username
        this.classe = classe
        this.password = password
    }

    constructor()
    constructor(
        username: String,
        password: String,
        position: Position,
        classe: String,
        AvatarRencontre: HashMap<String, Rencontre>
    ) {
        this.username = username
        this.password = password
        this.position = position
        this.classe = classe
        this.AvatarRencontre = AvatarRencontre
    }

    fun save() {
        val db = Firebase.firestore
        val profils = db.collection("user")

        profils.whereEqualTo("username", username).get().addOnCompleteListener {
            it.result.documents[0].reference.set(MainActivity.user);
        }
    }

}