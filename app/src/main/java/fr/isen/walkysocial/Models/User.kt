package fr.isen.walkysocial.Models

import android.content.ComponentCallbacks
import android.content.Intent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import fr.isen.walkysocial.MainActivity
import kotlin.random.Random

class User {

    var username: String = ""
    var uid: String = ""

    var coins: Int= 0

    var classe: UserClass = UserClass.TANK

    var HP: Int = 0
    var HP_Max: Int = 0
    var lastHPtake: Long = System.currentTimeMillis()

    var Atk : Int = 1
    var Def: Int = 1
    var Esq: Int = 1

    var position: Position = Position(0.0,0.0)
    var isConnected: Long = System.currentTimeMillis()

    var stockItems: HashMap<String, Int> = hashMapOf("attackPotion" to 0, "defensePotion" to 0, "PVPotion" to 0, "dodgePotion" to 0)

    var AvatarRencontre: HashMap<String, Rencontre> = HashMap()

    constructor(username: String, classe: UserClass, uid: String) {
        this.username = username
        this.classe = classe
        this.uid = uid
        this.HP = 500
        this.HP_Max = 500
        this.Atk = 1
        this.Def = 1
        this.Esq = 1
    }

    constructor()

    fun save() {
        this.isConnected = System.currentTimeMillis()

        val db = Firebase.firestore
        val profils = db.collection("user")

        profils.whereEqualTo("uid", uid).get().addOnCompleteListener {
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

    fun fight(boss: Boss, callback: (String) -> Unit): Boolean {
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
            }else {
                callback("Vous avez fait ${actualDamage} degat au boss. Il lui reste ${boss.HP} PV");
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
            }else {
                callback("Le boss vous as fait ${actualBossDamage} degat au boss. Il vous reste ${this.HP} PV");
            }
        }
        return false
    }

    fun getLifePercent():Double {
        return ((this.HP*100)/(this.HP_Max)).toDouble();
    }

    fun updateStats(user1: User, rencontre : Rencontre) {

        if(isFibonacci(rencontre.nombre_rencontre)) {
            when (user1.classe) {
                UserClass.ARCHER -> {
                    this.Esq ++
                    // Bloc de code à exécuter lorsque la variable est égale à valeur1
                }
                UserClass.TANK -> {
                    this.Def++
                    // Bloc de code à exécuter lorsque la variable est égale à valeur2
                }
                UserClass.GUERRIER -> {
                    this.Atk++;
                }
                UserClass.MAGE -> {
                    this.HP_Max++
                }
            }
        }
    }

    companion object {
        fun getUserByUid(uid: String, callback: (User) -> Unit) {
            var user = User()

            val db = Firebase.firestore
            val profils = db.collection("user")

            profils.whereEqualTo("uid", uid).get().addOnCompleteListener {
                if (it.result.size() == 0) {
                    callback(User())
                } else {
                    callback(it.result.documents[0].toObject(User::class.java)!!)
                }
            }
        }

        fun isFibonacci(number: Int): Boolean {
            var a = 0
            var b = 1

            while (b < number) {
                val temp = b
                b += a
                a = temp
            }

            return b == number
        }
    }

}