package fr.isen.monsterfighter.MonsterRecap

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.monsterfighter.Extensions.Extensions.dialog
import fr.isen.monsterfighter.Extensions.Extensions.toast
import fr.isen.monsterfighter.Model.Monster
import fr.isen.monsterfighter.Model.Parts
import fr.isen.monsterfighter.Model.User
import fr.isen.monsterfighter.MonsterCrea.MonsterCreationActivity
import fr.isen.monsterfighter.R
import fr.isen.monsterfighter.RegisterActivity
import fr.isen.monsterfighter.SignInActivity
import fr.isen.monsterfighter.databinding.ActivityMonsterRecapBinding
import fr.isen.monsterfighter.utils.FirebaseUtils
import fr.isen.monsterfighter.utils.FirebaseUtils.monsterRef
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef
import kotlin.reflect.typeOf


class MonsterRecapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonsterRecapBinding

    private lateinit var availablePartsList: ArrayList<Parts>
    private lateinit var availableMonsterList: ArrayList<Monster>
    private lateinit var availableMonsterMap: HashMap<String, Monster>
    private lateinit var adapter: RecapAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonsterRecapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        availableMonsterMap = HashMap()
        availableMonsterList = ArrayList()
        availablePartsList = ArrayList()


        loadMonsters()
        loadParts() //TODO <=en cache ou en utils
        adapter = RecapAdapter(availableMonsterList, availablePartsList)
        binding.recapRecycler.layoutManager = LinearLayoutManager(applicationContext)
        binding.recapRecycler.adapter = adapter

        binding.RecapNew.setOnClickListener {
            startActivity(Intent(this, MonsterCreationActivity::class.java))
            finish()
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onChanged() {
                for(i in availableMonsterMap){
                    if(!availableMonsterList.contains(i.value)){
                        Log.wtf("if", "we in boi") // but it not be gud...
                        //monsterRef.child(i.key).removeValue()
                        //availableMonsterMap.remove(i.key)
                    }
                }
                //userRef.child(getUserId()).child("listMonsters").setValue(availableMonsterMap)
                Log.wtf("dataChange", availableMonsterList.toString())
                super.onChanged()
            }
        })
    }

    private fun loadMonsters(){
        userRef.child(getUserId()).child("listMonsters").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        availableMonsterList.add(i.getValue(Monster::class.java)!!)
                    }
                    availableMonsterMap = snapshot.value as HashMap<String, Monster>
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