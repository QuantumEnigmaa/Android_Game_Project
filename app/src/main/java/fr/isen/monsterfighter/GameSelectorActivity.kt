package fr.isen.monsterfighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.isen.monsterfighter.databinding.ActivityGameSelectorBinding

class GameSelectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameSelectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}