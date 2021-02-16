package fr.isen.monsterfighter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import fr.isen.monsterfighter.Extensions.Extensions.toast
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
        if (identicalPassword()) {
            // identicalPassword() returns true only  when inputs are not empty and passwords are identical
            userEmail = binding.registerEmail.text.toString().trim()
            userName = binding.registerUsername.text.toString()
            userPassword = binding.registerPassword.text.toString().trim()

            /*create a user*/
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            toast("Votre compte a bien été créé !")
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
        val newUser = User(userName, null, 0, ArrayList(), ArrayList())
        userRef.child(userRef.push().key.toString()).setValue(newUser)
    }
}