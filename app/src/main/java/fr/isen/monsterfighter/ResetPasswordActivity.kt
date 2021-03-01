package fr.isen.monsterfighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.isen.monsterfighter.Extensions.Extensions.toast
import fr.isen.monsterfighter.databinding.ActivityResetPasswordBinding
import fr.isen.monsterfighter.utils.FirebaseUtils.firebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resetPasswordButton.setOnClickListener {
            val userEmail = binding.resetPasswordEmail.text.toString().trim()

            if (userEmail.isEmpty()) {
                toast("Veuillez entrer votre adresse e-mail")
            } else {
                firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener {
                    if (it.isSuccessful) {
                        toast("Regardez votre boite mail pour changer votre mot-de-passe")
                    } else {
                        toast("Une erreur est survenue lors de l'envoi du e-mail")
                    }
                }
            }
        }
    }
}