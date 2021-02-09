package fr.isen.monsterfighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.isen.monsterfighter.Animations.LoadingAnimation
import fr.isen.monsterfighter.databinding.ActivitySigninBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var loadingAnimation: LoadingAnimation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Loading screen
        loadingAnimation = LoadingAnimation(this, "loadingScreen.json")

        MainScope().launch {
            supportActionBar?.hide()
            loadingAnimation.playAnimation()
            delay(5500)
            loadingAnimation.stopAnimation(binding.root)
            supportActionBar?.show()
        }
    }
}