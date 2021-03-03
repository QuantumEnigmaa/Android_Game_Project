package fr.isen.monsterfighter

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.monsterfighter.Model.Lobby
import fr.isen.monsterfighter.Model.Monster
import fr.isen.monsterfighter.Model.Parts
import fr.isen.monsterfighter.Model.User
import fr.isen.monsterfighter.databinding.ActivityFightBinding
import fr.isen.monsterfighter.databinding.ActivityRegisterBinding
import fr.isen.monsterfighter.utils.FirebaseUtils.lobbyRef
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFightBinding
    private lateinit var lobbysList: ArrayList<Lobby>
    private lateinit var mymonsterList: ArrayList<Monster>
    private lateinit var ennemymonsterList: ArrayList<Monster>
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var me:String
    private lateinit var ennemy:String
    private var inALobby:Boolean=false
    private var lobbyNumber:Int=0
    private var lobbyFull:Boolean=false
    var player=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lobbysList= ArrayList()
        mymonsterList= ArrayList()
        ennemymonsterList= ArrayList()
        sharedPreferences= getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        me= sharedPreferences.getString("id","0").toString()
        loadLobbys()
        //loadUserData()

    }

    private fun updateStatsDisplay(){
        binding.yourStats.text="hp="+mymonsterList[0].mcurrentHp+
                "\n dexterite="+mymonsterList[0].mdext+
                "\n force="+mymonsterList[0].mstrength+
                "\n intelligence="+mymonsterList[0].mintel+
                "\n nom="+mymonsterList[0].mname+"\n dexterite"

        binding.enemyStats.text="hp="+ennemymonsterList[0].mcurrentHp+
                "\n dexterite="+ennemymonsterList[0].mdext+
                "\n force="+ennemymonsterList[0].mstrength+
                "\n intelligence="+ennemymonsterList[0].mintel+
                "\n nom="+ennemymonsterList[0].mname+"\n dexterite"
    }

    private fun joinLobby(){
        var index:Int = 0
        while (index<2) {
            Log.d("joining a lobby",lobbysList.size.toString())
            if (lobbysList[index].lActiveStatus == 1) {
                inALobby=true
                lobbyNumber=index+1
                lobbysList[index].lPlayer2Id= me
                lobbyRef.child((index+1).toString()).child("lActiveStatus").setValue(2)
                ennemy=lobbysList[index].lPlayer1Id
                lobbyRef.child((index+1).toString()).child("lPlayer2Id").setValue(me)
                player=2
                index=2

            }
            else{
                Log.d("index2",index.toString())
                if (lobbysList[index].lActiveStatus == 0) {
                    inALobby=true
                    lobbyNumber=index+1
                    lobbysList[index].lPlayer1Id= me
                    lobbyRef.child((index+1).toString()).child("lActiveStatus").setValue(1)
                    lobbyRef.child((index+1).toString()).child("lPlayer1Id").setValue(me)
                    player=1
                    index=2
                }
                else {
                    index += 1
                    if (index==1){
                        inALobby=false
                    }
                }
            }
        }
    }

    private fun loadUserData() {
        userRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.firstOrNull { it.key.toString() == me }?.let {it.getValue()
                            it.children.firstOrNull{ it.key.toString()=="listMonsters"}?.let{
                                for (i in it.children){
                                    mymonsterList.add(i.getValue(Monster::class.java )!!)
                                }
                                stateOfGame()
                                Log.d("monstre",mymonsterList[0].toString())
                            }
                        }
                        snapshot.children.firstOrNull { it.key.toString() == ennemy }?.let {
                            it.getValue()
                            it.children.firstOrNull { it.key.toString() == "listMonsters" }?.let {
                                for (i in it.children) {
                                    ennemymonsterList.add(i.getValue(Monster::class.java)!!)
                                }
                                //updateStatsDisplay()
                                Log.d("monstre", ennemymonsterList[0].toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i("LoadUserDataError", error.toString())
                    }
                }
        )
    }

    private fun stateOfGame(){
        if (!inALobby){
            Log.d("joining",inALobby.toString())
            inALobby=true
            joinLobby()
        }
        else{
            if(!lobbyFull)
            {
                if ((lobbysList[lobbyNumber].lPlayer1Id!="") && (lobbysList[lobbyNumber].lPlayer2Id!=""))
                {
                    lobbyFull=true
                }
            }
            else{
                if((ennemymonsterList.isNotEmpty()) && (mymonsterList.isNotEmpty())){
                    updateStatsDisplay()
                }
                else{
                    loadUserData()
                }
            }
        }
    }

    private fun loadLobbys() {
        lobbyRef.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lobbysList.clear()
                    for (i in snapshot.children) {
                        lobbysList.add(i.getValue(Lobby::class.java)!!)
                    }
                    Log.d("list lobby", lobbysList[1].lPlayer1Id.toString())
                    stateOfGame()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("lobby loading error", error.toString())
                }
            }
        )
    }
}