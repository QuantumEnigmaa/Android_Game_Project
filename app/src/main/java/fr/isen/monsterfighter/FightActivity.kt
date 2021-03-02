package fr.isen.monsterfighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.monsterfighter.Model.Lobby
import fr.isen.monsterfighter.Model.Parts
import fr.isen.monsterfighter.databinding.ActivityFightBinding
import fr.isen.monsterfighter.databinding.ActivityRegisterBinding
import fr.isen.monsterfighter.utils.FirebaseUtils.lobbyRef

class FightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFightBinding
    private lateinit var lobbysList: ArrayList<Lobby>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lobbysList= ArrayList()
        loadLobbys()
    }

    private fun loadLobbys() {
        lobbyRef.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        lobbysList.add(i.getValue(Lobby::class.java)!!)
                        //Log.d("list lobby", lobbysList.toString())
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("lobby loading error", error.toString())
                }
            }
        )
    }
}