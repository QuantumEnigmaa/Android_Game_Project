package fr.isen.monsterfighter.Model

class User {
    var userName: String? = null
    var profileImageUrl: String? = null
    var userLvl: Int = 0
    var listMonsters: HashMap<String, Monster> = hashMapOf()
    var selectedMonsters: String? = null

    constructor() {}

    constructor(name: String, imageUrl: String?, lvl: Int, monsterList: HashMap<String, Monster>, currentMonster: String?) {
        this.userName = name
        this.profileImageUrl = imageUrl
        this.userLvl = lvl
        this.listMonsters = monsterList
        this.selectedMonsters = currentMonster
    }

}