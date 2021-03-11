package fr.isen.monsterfighter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.monsterfighter.Model.User
import fr.isen.monsterfighter.databinding.ActivityHomeBinding
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef
import java.nio.channels.Channel


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

        /*private fun createNotificationChannel() {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.channel_name)
                val descriptionText = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_key)
            .setContentTitle(R.string.app_name.toString())
            .setContentText("Revenez jouer")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)*/


        // The user wants to check his profile
        binding.homeProfileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // The  user wants to check/create his monster
        val intentCreat = Intent(this, MonsterCreationActivity::class.java)
        val intentCheck = Intent(this, MonsterRecapActivity::class.java)
        binding.homePartyButton.setOnClickListener {
            userRef.addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.firstOrNull { it.key.toString() == getUserId() }?.let {
                                it.getValue(User::class.java)?.let {
                                    //TODO PUT INTENTCHECK ON ELSE
                                    if (it.listMonsters.isEmpty()) startActivity(intentCreat) else startActivity(intentCreat)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.i("current user error", error.toString())
                        }
                    }
            )
        }
    }

    private fun getUserId(): String {
        // accessing cache
        val sharedPreferences = getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        return sharedPreferences.getString(RegisterActivity.USER_ID, "")!!
    }
}