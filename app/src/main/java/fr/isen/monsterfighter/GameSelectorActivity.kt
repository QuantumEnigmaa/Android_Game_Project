package fr.isen.monsterfighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import fr.isen.monsterfighter.Extensions.Extensions.dialog
import fr.isen.monsterfighter.databinding.ActivityGameSelectorBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameSelectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameSelectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dungeon mode button
        binding.selectorSoloButton.setOnClickListener {
            dialog("Fonctionnalitéen cours de développement", "Indisponible", true) {}
        }

        // Multiplayer button
        binding.selectorMutliplayer.setOnClickListener{
            MainScope().launch {
                binding.selectorAnimation.setAnimation("battleAnimation.json")
                binding.selectorAnimation.playAnimation()
                delay(2000)
                binding.selectorAnimation.cancelAnimation()
                binding.selectorAnimation.visibility = View.GONE
            }
        }

    }
}