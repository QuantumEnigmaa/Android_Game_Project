package fr.isen.monsterfighter.MonsterRecap

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.monsterfighter.MenuActivity
import fr.isen.monsterfighter.Model.Monster
import fr.isen.monsterfighter.Model.Parts
import fr.isen.monsterfighter.MonsterCrea.MonsterCreationActivity
import fr.isen.monsterfighter.R
import fr.isen.monsterfighter.RegisterActivity
import fr.isen.monsterfighter.databinding.ActivityMonsterRecapBinding
import fr.isen.monsterfighter.utils.FirebaseUtils
import fr.isen.monsterfighter.utils.FirebaseUtils.monsterRef
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef


class MonsterRecapActivity : MenuActivity() {

    private lateinit var binding: ActivityMonsterRecapBinding

    private lateinit var availablePartsList: ArrayList<Parts>
    private lateinit var availableMonsterList: ArrayList<Monster>
    private lateinit var adapter: RecapAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonsterRecapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        availableMonsterList = ArrayList()
        availablePartsList = ArrayList()


        loadMonsters()
        loadParts() //TODO <=en cache ou en utils
        adapter = RecapAdapter(availableMonsterList, availablePartsList, this)
        binding.recapRecycler.layoutManager = LinearLayoutManager(applicationContext)
        binding.recapRecycler.adapter = adapter

        binding.RecapNew.setOnClickListener {
            startActivity(Intent(this, MonsterCreationActivity::class.java))
            finish()
        }

        loadName()
        binding.MonsterUsedName.text = "Aucun monstre choisi"
    }

    override fun onStop(){
        finish()
        super.onStop()
    }

    override fun onPause(){
        finish()
        super.onPause()
    }


    private fun loadName() {
        userRef.child(getUserId()).child("selectedMonsters").addValueEventListener(
                object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.value.toString().let{
                            monsterRef.child(it).child("mname").get().addOnSuccessListener{
                                if (snapshot.exists()) {
                                    var monsterName = it.value.toString()
                                    binding.MonsterUsedName.text = "Vous combattez avec $monsterName"
                                }else{
                                    binding.MonsterUsedName.text = "Aucun monstre choisi"
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.i("LoadUserDataError", error.toString())
                    }
                }
        )
    }

    private fun loadMonsters(){
        userRef.child(getUserId()).child("listMonsters").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        availableMonsterList.add(i.getValue(Monster::class.java)!!)
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.i("parts loading error", error.toString())
                }
            }
        )
    }

    //mmmhh le bon code duliqué
    private fun loadParts() {
        FirebaseUtils.partsRef.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        availablePartsList.add(i.getValue(Parts::class.java)!!)
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.i("parts loading error", error.toString())
                }
            }
        )
    }

    fun getUserId(): String {
        // accessing cache
        val sharedPreferences = getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        return sharedPreferences.getString(RegisterActivity.USER_ID, "")!!
    }
}