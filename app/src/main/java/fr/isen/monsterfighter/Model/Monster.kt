package fr.isen.monsterfighter.Model

data class Monster (
    var mCurrentHp: Int = 0,
    var mLstPartsId: ArrayList<Parts> = ArrayList(),
    var mName: String = "",
    var mStrength: Int = 0,
    var mIntel: Int = 0,
    var mDext: Int = 0
)