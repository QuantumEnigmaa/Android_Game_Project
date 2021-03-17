package fr.isen.monsterfighter.Fight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.monsterfighter.HomeActivity
import fr.isen.monsterfighter.Model.User
import fr.isen.monsterfighter.R
import fr.isen.monsterfighter.RegisterActivity
import fr.isen.monsterfighter.databinding.ActivityFightRecapBinding
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FightRecapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFightRecapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFightRecapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Retrieving data from fight activity
        val state = intent.getBooleanExtra(FightActivity.STATE, false)

        if (!state) {
            binding.fightRecapTitle.text = getString(R.string.win)
            binding.fightRecapXP.text = WIN_XP_POINTS.toString()
            updateUserXp(WIN_XP_POINTS)
        }
        else {
            binding.fightRecapTitle.text = getString(R.string.loss)
            binding.fightRecapXP.text = LOSS_XP_POINTS.toString()
            updateUserXp(LOSS_XP_POINTS)
        }

        binding.fightRecapMenuButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.fightRecapReplayButton.setOnClickListener {
            startActivity(Intent(this, FightActivity::class.java))
            finish()
        }
    }

    private fun updateUserXp(xpPoints: Int) {
        val userXp = loadUserXP()
        if (userXp == 100) {
            userRef.child(getUserId()).child("userLvl").setValue(0)

            //Update user lvl
            val userLvl = getUserCurrentLvl()
            getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE).edit().putInt(RegisterActivity.USER_ID, userLvl+1).apply()
        } else {
            userRef.child(getUserId()).child("userLvl").setValue(userXp+ xpPoints)
        }
    }

    private fun loadUserXP(): Int {
        var xp = 0
        userRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.firstOrNull { it.key.toString() == getUserId() }?.let {
                            it.getValue(User::class.java)?.let { u ->
                                xp = u.userLvl
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i("LoadUserDataError", error.toString())
                    }
                }
        )
        return xp
    }

    fun getUserId(): String {
        // accessing cache
        val sharedPreferences = getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        return sharedPreferences.getString(RegisterActivity.USER_ID, "")!!
    }

    fun getUserCurrentLvl(): Int {
        val sharedPreferences = getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        return sharedPreferences.getInt(RegisterActivity.USER_CURRENT_LVL, 0)
    }

    companion object {
        const val WIN_XP_POINTS = 50
        const val LOSS_XP_POINTS = 25
    }
}