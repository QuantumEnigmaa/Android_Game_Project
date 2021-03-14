package fr.isen.monsterfighter.Model

class User {
    var userName: String? = null
    var profileImageUrl: String? = null
    var userLvl: Int = 0
    var listMonsters: HashMap<String, Monster> = HashMap()
    var listSelectedMonsters: ArrayList<Monster> = ArrayList()

    constructor() {}

    constructor(name: String, imageUrl: String?, lvl: Int, monsterList: HashMap<String, Monster>, currentMonsterList: ArrayList<Monster>) {
        this.userName = name
        this.profileImageUrl = imageUrl
        this.userLvl = lvl
        this.listMonsters = monsterList
        this.listSelectedMonsters = currentMonsterList
    }

}