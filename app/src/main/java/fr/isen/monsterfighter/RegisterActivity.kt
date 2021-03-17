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
import android.widget.EditText
import fr.isen.monsterfighter.Extensions.Extensions.toast
import fr.isen.monsterfighter.Model.Monster
import fr.isen.monsterfighter.Model.User
import fr.isen.monsterfighter.databinding.ActivityRegisterBinding
import fr.isen.monsterfighter.utils.FirebaseUtils.firebaseAuth
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    lateinit var userEmail: String
    lateinit var userPassword: String
    lateinit var userName: String
    lateinit var createAccountInputsArray: Array<EditText>

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "fr.isen.monsterfighter"
    private val description = "Compte créé avec succès!"

    companion object {
        const val USER_PREF = "userPref"
        const val USER_ID = "id"
        const val USER_CURRENT_LVL = "currentLvl"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createAccountInputsArray = arrayOf(binding.registerEmail, binding.registerUsername, binding.registerPassword, binding.registerPasswordAgain)
        binding.registerButton.setOnClickListener {
            createAccount()
        }

    }

    private fun notEmpty(): Boolean = binding.registerEmail.text.toString().trim().isNotEmpty() &&
            binding.registerUsername.text.toString().trim().isNotEmpty() &&
            binding.registerPassword.text.toString().trim().isNotEmpty() &&
            binding.registerPasswordAgain.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() &&
                binding.registerPassword.text.toString().trim() == binding.registerPasswordAgain.text.toString().trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} est nécessaire"
                }
            }
        } else {
            toast("Les mots de passe ne correspondent pas !")
        }
        return identical
    }

    private fun createAccount() {
        //Init Notif Manager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (identicalPassword()) {
            // identicalPassword() returns true only  when inputs are not empty and passwords are identical
            userEmail = binding.registerEmail.text.toString().trim()
            userName = binding.registerUsername.text.toString()
            userPassword = binding.registerPassword.text.toString().trim()

            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            /*create a user*/
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            toast("Votre compte a bien été créé !")
                            //Notification
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                notificationChannel= NotificationChannel(channelId,description,NotificationManager.IMPORTANCE_HIGH)
                                notificationChannel.enableLights(true)
                                notificationChannel.lightColor= Color.GREEN
                                notificationChannel.enableVibration(false)
                                notificationManager.createNotificationChannel(notificationChannel)

                                builder = Notification.Builder(this,channelId)
                                        .setContentTitle("Compte Créé")
                                        .setContentText("Compte créé avec succès!")
                                        .setSmallIcon(R.mipmap.ic_launcher_round)
                                        .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.mipmap.ic_launcher))
                                        .setContentIntent(pendingIntent)
                            }else{
                                builder = Notification.Builder(this)
                                        .setContentTitle("Compte Créé")
                                        .setContentText("Compte créé avec succès!")
                                        .setSmallIcon(R.mipmap.ic_launcher_round)
                                        .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.mipmap.ic_launcher))
                                        .setContentIntent(pendingIntent)
                            }
                            notificationManager.notify(1234,builder.build())
                            // sendEmailVerification()
                            startActivity(Intent(this, HomeActivity::class.java))

                            finish()
                            createUser()
                        } else {
                            toast("Erreur lors de la création")
                        }
                    }
        }
    }

    private fun createUser() {
        val fbUserID = firebaseAuth.currentUser?.uid.toString()
        val newUser = User(userName, "", 0, HashMap(), ArrayList())
        userRef.child(fbUserID).setValue(newUser)

        // Storing the userID  (and Current lvl in the cache)->not anymore /!\
        val sharedPreferences = getSharedPreferences(USER_PREF, MODE_PRIVATE)
        sharedPreferences.edit().putString(USER_ID, fbUserID).apply()
    }
}