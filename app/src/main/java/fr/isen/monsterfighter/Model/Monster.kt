package fr.isen.monsterfighter.Model

data class Monster (
    var mcurrentHp: Int = 0,
    var mlstPartsId: ArrayList<Int> = ArrayList(),
    var mname: String = "",
    var mstrength: Int = 0,
    var mintel: Int = 0,
    var mdext: Int = 0
)