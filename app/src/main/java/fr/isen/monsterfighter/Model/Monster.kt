package fr.isen.monsterfighter.Model

data class Monster (var mCurrentHp: Int, val mLstPartsId: ArrayList<Parts>, val mName: String,
val mStrength: Int, val mIntel: Int, val mDext: Int)