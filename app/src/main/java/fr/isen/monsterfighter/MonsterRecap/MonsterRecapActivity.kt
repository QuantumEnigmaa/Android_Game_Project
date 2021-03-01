package fr.isen.monsterfighter.MonsterRecap

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.monsterfighter.Extensions.Extensions.dialog
import fr.isen.monsterfighter.Extensions.Extensions.toast
import fr.isen.monsterfighter.Model.Monster
import fr.isen.monsterfighter.Model.Parts
import fr.isen.monsterfighter.MonsterCrea.MonsterCreationActivity
import fr.isen.monsterfighter.MonsterCrea.PartAdapter
import fr.isen.monsterfighter.R
import fr.isen.monsterfighter.RegisterActivity
import fr.isen.monsterfighter.SignInActivity
import fr.isen.monsterfighter.databinding.ActivityMonsterRecapBinding
import fr.isen.monsterfighter.utils.FirebaseUtils
import fr.isen.monsterfighter.MonsterRecap.RecapAdapter


class MonsterRecapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonsterRecapBinding

    private lateinit var availableMonsterList: ArrayList<Monster>
    private lateinit var adapter: RecapAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonsterRecapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        availableMonsterList = ArrayList()


        loadMonsters()
        //TODO same as MonsterCreaActi
        binding.RecapRecycler.layoutManager = LinearLayoutManager(applicationContext)
        binding.RecapRecycler.adapter = adapter
        adapter.notifyDataSetChanged()

        binding.RecapNew.setOnClickListener {
            startActivity(Intent(this, MonsterCreationActivity::class.java))
        }
    }

    private fun loadMonsters(){
        var availableMonsterListID = ArrayList<String>()
        FirebaseUtils.userRef.child(getUserId()).child("listMonsters").addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in snapshot.children) {
                            availableMonsterListID.add(i.key.toString())
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.i("parts loading error", error.toString())
                    }
                }
        )
        adapter = RecapAdapter(availableMonsterList)
    }

    fun getUserId(): String {
        // accessing cache
        val sharedPreferences = getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        return sharedPreferences.getString(RegisterActivity.USER_ID, "")!!
    }
}