package fr.isen.monsterfighter.Fight

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.monsterfighter.Model.Lobby
import fr.isen.monsterfighter.Model.Monster
import fr.isen.monsterfighter.RegisterActivity
import fr.isen.monsterfighter.databinding.ActivityFightBinding
import fr.isen.monsterfighter.utils.FirebaseUtils.lobbyRef
import fr.isen.monsterfighter.utils.FirebaseUtils.monsterRef
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef

class FightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFightBinding

    private lateinit var lobbysList: ArrayList<Lobby>
    private lateinit var mymonsterList: ArrayList<Monster>
    private lateinit var mymonsterListKeys: ArrayList<String>
    private lateinit var ennemymonsterList: ArrayList<Monster>
    private lateinit var ennemymonsterListKeys: ArrayList<String>
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var me:String
    private var ennemy:String=""
    private var inALobby:Boolean=false
    private var lobbyNumber:Int=0
    private var lobbyFull:Boolean=false
    var player=0
    private var gameOver:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lobbysList= ArrayList()
        mymonsterList= ArrayList()
        mymonsterListKeys= ArrayList()
        ennemymonsterList= ArrayList()
        ennemymonsterListKeys= ArrayList()

        sharedPreferences= getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        me= sharedPreferences.getString("id","0").toString()

        loadLobbys()

        binding.attack1.setOnClickListener(){
            if(lobbysList[lobbyNumber-1].lTurn==player){
                userRef.child(ennemy).child("listMonsters").child(ennemymonsterListKeys[0]).child("mcurrentHp").setValue(ennemymonsterList[0].mcurrentHp-10)
                if(player==1){
                    lobbyRef.child(lobbyNumber.toString()).child("lTurn").setValue(2)
                }
                else{
                    lobbyRef.child(lobbyNumber.toString()).child("lTurn").setValue(1)
                }

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        resetLobbys()
    }

    private fun resetMonsters() {
        monsterRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.firstOrNull { it.key.toString() == mymonsterListKeys[0] }?.let {
                            it.getValue(Monster::class.java)?.let {
                                userRef.child(me).child("listMonsters").child(mymonsterListKeys[0]).child("mcurrentHp").setValue(it.mcurrentHp)
                            }
                        }
                        snapshot.children.firstOrNull { it.key.toString() == ennemymonsterListKeys[0] }?.let {
                            it.getValue(Monster::class.java)?.let {
                                userRef.child(ennemy).child("listMonsters").child(ennemymonsterListKeys[0]).child("mcurrentHp").setValue(it.mcurrentHp)
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.d("lobby loading error", error.toString())
                    }
                }
        )
    }

    private fun resetLobbys(){
        lobbyRef.child(lobbyNumber.toString()).child("lPlayer2Id").setValue("")
        lobbyRef.child(lobbyNumber.toString()).child("lPlayer1Id").setValue("")
        lobbyRef.child(lobbyNumber.toString()).child("lActiveStatus").setValue(0)
        lobbyRef.child(lobbyNumber.toString()).child("lTurn").setValue(0)
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
                lobbyRef.child(lobbyNumber.toString()).child("lPlayer2Id").setValue(me)
                lobbyRef.child(lobbyNumber.toString()).child("lActiveStatus").setValue(2)
                player=2
                index=2

            }
            else{
                Log.d("index2",index.toString())
                if (lobbysList[index].lActiveStatus == 0) {
                    inALobby=true
                    lobbyNumber=index+1
                    lobbysList[index].lPlayer1Id= me
                    lobbyRef.child(lobbyNumber.toString()).child("lActiveStatus").setValue(1)
                    lobbyRef.child(lobbyNumber.toString()).child("lPlayer1Id").setValue(me)
                    player=1
                    index=2
                }
                else {
                    index += 1
                    inALobby=false
                }
            }
        }
    }

    private fun loadUserData() {
        userRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!gameOver){
                            snapshot.children.firstOrNull { it.key.toString() == me }?.let {
                                it.getValue()
                                it.children.firstOrNull{ it.key.toString()=="listMonsters"}?.let{
                                    mymonsterList.clear()
                                    for (i in it.children){
                                        mymonsterList.add(i.getValue(Monster::class.java )!!)
                                        mymonsterListKeys.add(i.key!!)
                                    }
                                }
                            }
                            snapshot.children.firstOrNull { it.key.toString() == ennemy }?.let { it.getValue()
                                it.children.firstOrNull { it.key.toString() == "listMonsters" }?.let {
                                    ennemymonsterList.clear()
                                    for (i in it.children) {
                                        ennemymonsterList.add(i.getValue(Monster::class.java)!!)
                                        ennemymonsterListKeys.add(i.key!!)
                                    }
                                    //updateStatsDisplay()
                                }
                            }
                            stateOfGame()
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
            LobbyHandling()
        }
    }

    private fun LobbyHandling(){
        if(!lobbyFull)
        {
            //if ((lobbysList[lobbyNumber].lPlayer1Id!="") && (lobbysList[lobbyNumber].lPlayer2Id!=""))
            if ((ennemy=="") && (lobbysList[lobbyNumber-1].lPlayer2Id!=""))
            {
                lobbyFull=true
                if (player==1){
                    ennemy=lobbysList[lobbyNumber-1].lPlayer2Id
                }
                else{
                    ennemy=lobbysList[lobbyNumber-1].lPlayer1Id
                }
            }
        }
        else{
            if(lobbysList[lobbyNumber-1].lTurn==0)
            {
                lobbyRef.child(lobbyNumber.toString()).child("lTurn").setValue(1)
            }
            GameHandling()
        }
    }

    private fun GameHandling(){
        if((ennemymonsterList.isNotEmpty()) && (mymonsterList.isNotEmpty())){
            updateStatsDisplay()
            if((mymonsterList[0].mcurrentHp<=0) || (ennemymonsterList[0].mcurrentHp<=0)){
                gameOver=true
                if(mymonsterList[0].mcurrentHp<=0){
                    binding.fightLog.text="Perdu"
                }
                else{
                    binding.fightLog.text="gagné"
                }
                if(player==1){
                    resetMonsters()
                    resetLobbys()
                }
                startActivity(Intent(this, FightRecapActivity::class.java))
                finish()
            }
        }
        else{
            loadUserData()
        }
    }

    private fun loadLobbys() {
        lobbyRef.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!gameOver){
                        lobbysList.clear()
                        for (i in snapshot.children) {
                            lobbysList.add(i.getValue(Lobby::class.java)!!)
                        }
                        stateOfGame()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("lobby loading error", error.toString())
                }
            }
        )
    }
}