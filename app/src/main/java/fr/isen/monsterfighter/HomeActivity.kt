package fr.isen.monsterfighter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import fr.isen.monsterfighter.MonsterCrea.MonsterCreationActivity
import fr.isen.monsterfighter.MonsterRecap.MonsterRecapActivity
import fr.isen.monsterfighter.databinding.ActivityHomeBinding
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef
import java.nio.channels.Channel


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "fr.isen.monsterfighter"
    private val description = "Test Notification"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // The user wants to play
        binding.homeGameButton.setOnClickListener {
            startActivity(Intent(this, GameSelectorActivity::class.java))
        }

        //Init Notif Manager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // The user wants to check his profile
        binding.homeProfileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            //Test notification temporairement sur ce bouton afin de simplififer TODO
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notificationChannel= NotificationChannel(channelId,description,NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor= Color.GREEN
                notificationChannel.enableVibration(false)
                notificationManager.createNotificationChannel(notificationChannel)

                builder = Notification.Builder(this,channelId)
                        .setContentTitle("Code")
                        .setContentText("Test Notif")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.mipmap.ic_launcher))
                        .setContentIntent(pendingIntent)
            }else{
                builder = Notification.Builder(this)
                        .setContentTitle("Code")
                        .setContentText("Test Notif")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.mipmap.ic_launcher))
                        .setContentIntent(pendingIntent)
            }
            notificationManager.notify(1234,builder.build())

        }

        // The  user wants to check/create his monster
        val intentCreat = Intent(this, MonsterCreationActivity::class.java)
        val intentCheck = Intent(this, MonsterRecapActivity::class.java)
        binding.homePartyButton.setOnClickListener {
            //start either recap or create monster depending on whether listMonster already exist
            // (<=> some monster were already created)
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