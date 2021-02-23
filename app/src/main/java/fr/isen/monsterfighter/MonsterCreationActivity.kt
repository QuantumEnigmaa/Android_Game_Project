package fr.isen.monsterfighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.isen.monsterfighter.databinding.ActivityMonsterCreationBinding

class MonsterCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonsterCreationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonsterCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}