package fr.isen.monsterfighter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.monsterfighter.MonsterCrea.MonsterCreationActivity
import fr.isen.monsterfighter.MonsterRecap.MonsterRecapActivity
import fr.isen.monsterfighter.databinding.ActivityHomeBinding
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // The user wants to play
        binding.homeGameButton.setOnClickListener {
            startActivity(Intent(this, GameSelectorActivity::class.java))
        }


        // The user wants to check his profile
        binding.homeProfileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // The  user wants to check/create his monster
        val intentCreat = Intent(this, MonsterCreationActivity::class.java)
        val intentCheck = Intent(this, MonsterRecapActivity::class.java)
        binding.homePartyButton.setOnClickListener {


            //TODO Better getUserId (import from utils or model)
            userRef.child(getUserId()).child("listMonsters").addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()) startActivity(intentCheck) else startActivity(intentCreat)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.i("current user error", error.toString())
                        }
                    }
            )
        }
    }

    //TODO put in model or utils cuz code duplication
    private fun getUserId(): String {
        // accessing cache
        val sharedPreferences = getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        return sharedPreferences.getString(RegisterActivity.USER_ID, "")!!
    }
}