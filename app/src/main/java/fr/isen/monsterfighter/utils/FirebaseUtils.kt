package fr.isen.monsterfighter.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
    private val firebaseDB: FirebaseDatabase = FirebaseDatabase.getInstance()
    val lobbyRef: DatabaseReference = firebaseDB.getReference("Lobby")
    val partsRef: DatabaseReference = firebaseDB.getReference("Parts")
    val monsterRef: DatabaseReference = firebaseDB.getReference("monster")
    val userRef: DatabaseReference = firebaseDB.getReference("user")
}