package fr.isen.monsterfighter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.firebase.auth.FirebaseUser
import fr.isen.monsterfighter.Animations.LoadingAnimation
import fr.isen.monsterfighter.Extensions.Extensions.toast
import fr.isen.monsterfighter.utils.FirebaseUtils.firebaseAuth
import fr.isen.monsterfighter.databinding.ActivitySigninBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var loadingAnimation: LoadingAnimation

    lateinit var signInUsername: String
    lateinit var signInPassword: String
    lateinit var signInInputsArray: Array<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Loading screen
        loadingAnimation = LoadingAnimation(this, "loadingScreen.json")

        // If the user doesn't remember his password
        binding.accountLoginPwdForgotten.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        // If the user do not have an account
        binding.accountLoginNew.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // If the user has an account and wants to sign in
        signInInputsArray = arrayOf(binding.accountLoginEmail, binding.accountLoginPassword)
        binding.accountLoginButton.setOnClickListener {
            singInUser()
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, HomeActivity::class.java)

        MainScope().launch {
            //TODO PUT LINE 56 TO 60 BETWEEN /**/ WHILE DEVLOPING TO GAIN TIME
            /*supportActionBar?.hide()
            loadingAnimation.playAnimation()
            delay(5500)
            loadingAnimation.stopAnimation(binding.root)
            supportActionBar?.show()*/
            val user: FirebaseUser? = firebaseAuth.currentUser
            user?.let {
                startActivity(intent)
                toast("Bienvenue !")
            }
        }
    }

    private fun notEmpty(): Boolean = signInUsername.isNotEmpty() && signInPassword.isNotEmpty()

    private fun singInUser() {
        signInUsername = binding.accountLoginEmail.text.toString().trim()
        signInPassword = binding.accountLoginPassword.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInUsername, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        toast("Vous êtes authentifié !")
                        finish()
                    } else {
                        toast("Erreur d'authentification")
                    }
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} requis"
                }
            }
        }
    }
}