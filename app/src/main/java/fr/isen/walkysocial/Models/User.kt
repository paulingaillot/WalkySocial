package fr.isen.walkysocial.Models

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fr.isen.walkysocial.MainActivity
import kotlin.random.Random

class User {

    var username: String = ""
    var password: String = ""

    var classe: UserClass = UserClass.TANK

    var HP: Int = 0
    var HP_Max: Int = 0
    var lastHPtake: Long = System.currentTimeMillis()

    var Atk : Int = 1
    var Def: Int = 1

    var position: Position = Position(0.0,0.0)
    var isConnected: Long = System.currentTimeMillis()

    var AvatarRencontre: HashMap<String, Rencontre> = HashMap()

    constructor(username: String, classe: UserClass, password: String) {
        this.username = username
        this.classe = classe
        this.password = password
        this.HP = 500
        this.HP_Max = 500
        this.Atk = 1
        this.Def = 1
    }

    constructor()

    fun save() {
        val db = Firebase.firestore
        val profils = db.collection("user")

        profils.whereEqualTo("username", username).get().addOnCompleteListener {
            it.result.documents[0].reference.set(MainActivity.user);
        }
    }

    public fun updateHP() {
        var delay :Double = (System.currentTimeMillis() - this.lastHPtake).toDouble();
        if((delay / 60000)> 1 && HP < HP_Max) {
            HP += (delay / 60000).toInt()
            if(HP> HP_Max) HP = HP_Max
            this.lastHPtake = System.currentTimeMillis()
        }
    }

    fun fight(boss: Boss): Boolean {
        while (this.HP > 0 && boss.HP > 0) {
            // L'utilisateur attaque le boss
            var userDamage=1
            if(this.Atk != 1) userDamage = Random.nextInt(1, this.Atk)
            val bossDefense = boss.def
            var actualDamage = maxOf(userDamage - bossDefense, 1)
            actualDamage = minOf(actualDamage, (boss.max_HP*0.1).toInt())
            boss.HP -= actualDamage
            if (boss.HP <= 0) {
                return true
            }

            // Le boss attaque l'utilisateur
            var bossDamage = 1
            if(boss.atk != 1 ) bossDamage = Random.nextInt(1, boss.atk)
            val userDefense = this.Def
            var actualBossDamage = maxOf(bossDamage - userDefense, 1)
            actualBossDamage = minOf(actualBossDamage, (this.HP_Max*0.1).toInt())
            this.HP -= actualBossDamage
            if (this.HP <= 0) {
                return false
            }
        }
        return false
    }

    fun getLifePercent():Double {
        return ((this.HP*100)/(this.HP_Max)).toDouble();
    }

}