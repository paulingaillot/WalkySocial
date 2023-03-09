package fr.isen.walkysocial.Models

class User {


    var macAdress: String = ""

    var username: String = ""
    var classe: String = ""

    var AvatarRencontre: HashMap<String, Int> = HashMap()

    constructor(username: String, classe: String, macAddress: String) {
        this.username = username
        this.classe = classe
        this.macAdress = macAddress
    }

}