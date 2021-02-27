package fr.isen.monsterfighter.utils

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import fr.isen.monsterfighter.RegisterActivity

object FirebaseUtils : AppCompatActivity() {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB: FirebaseDatabase = FirebaseDatabase.getInstance()
    val lobbyRef: DatabaseReference = firebaseDB.getReference("Lobby")
    val partsRef: DatabaseReference = firebaseDB.getReference("Parts")
    val monsterRef: DatabaseReference = firebaseDB.getReference("monster")
    val userRef: DatabaseReference = firebaseDB.getReference("user")
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageRef: StorageReference = firebaseStorage.getReference("profileImages")

    /*fun getUserId(): String {
        // accessing cache
        val sharedPreferences = getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        return sharedPreferences.getString(RegisterActivity.USER_ID, "")!!
    }

    fun getUserCurrentLvl(): Int {
        val sharedPreferences = getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        return sharedPreferences.getInt(RegisterActivity.USER_CURRENT_LVL, 0)
    }*/
}