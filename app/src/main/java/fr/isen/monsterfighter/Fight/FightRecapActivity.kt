package fr.isen.monsterfighter.Fight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.isen.monsterfighter.databinding.ActivityFightRecapBinding

class FightRecapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFightRecapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFightRecapBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}