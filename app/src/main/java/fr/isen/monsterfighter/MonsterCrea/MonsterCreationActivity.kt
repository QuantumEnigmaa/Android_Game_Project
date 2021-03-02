package fr.isen.monsterfighter.MonsterCrea

//import fr.isen.monsterfighter.utils.FirebaseUtils.getUserId
import android.R.attr.entries
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.monsterfighter.Model.Monster
import fr.isen.monsterfighter.Model.Parts
import fr.isen.monsterfighter.RegisterActivity
import fr.isen.monsterfighter.databinding.ActivityMonsterCreationBinding
import fr.isen.monsterfighter.utils.FirebaseUtils.monsterRef
import fr.isen.monsterfighter.utils.FirebaseUtils.partsRef
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef


//TODO Clean this messy code
class MonsterCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonsterCreationBinding

    private lateinit var availablePartsList: ArrayList<Parts>
    private lateinit var partsListUsed: ArrayList<Parts>
    private lateinit var adapter: PartAdapter

    private val MAX_SLOTS = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonsterCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO where did monsterCreationSeparator go?

        // Fetching monster parts data from Firebase
        partsListUsed = ArrayList()
        availablePartsList = ArrayList()

        loadParts()
        adapter = PartAdapter(partsListUsed, availablePartsList)
        binding.partsRecycler.layoutManager = LinearLayoutManager(applicationContext)
        binding.partsRecycler.adapter = adapter


        //TODO worship function best function
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onChanged() {
                updateUI()
                super.onChanged()
            }
        })

        initUI()

        binding.monsterCreationCurrentSlots.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        setTextColor(s)
                    }
                    override fun afterTextChanged(s: Editable?) {
                    }
                }
        )

        // Handling buttons
        binding.monsterCreationSave.setOnClickListener {
            uploadMonster(availablePartsList, partsListUsed)
        }
        binding.monsterNewPart.setOnClickListener {
            partsListUsed.add(availablePartsList[0])
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
    private fun setTextColor(s: CharSequence?) {
        if (s.toString().toInt() > MAX_SLOTS) {
            binding.monsterCreationCurrentSlots.setTextColor(Color.RED)
            binding.monsterCreationSlash.setTextColor(Color.RED)
            binding.monsterCreationMaxSlots.setTextColor(Color.RED)
        } else {
            binding.monsterCreationCurrentSlots.setTextColor(Color.GRAY)
            binding.monsterCreationSlash.setTextColor(Color.GRAY)
            binding.monsterCreationMaxSlots.setTextColor(Color.GRAY)
        }
    }

    private fun initUI() {
        var i = 0
        var strength = 0
        var dext = 0
        var intel = 0
        var hp = 0
        var slot = 0

        binding.monsterCreationStatsStrengh.text = strength.toString()
        binding.monsterCreationStatsIntel.text = dext.toString()
        binding.monsterCreationStatsDext.text = intel.toString()
        binding.monsterCreationStatsHp.text = hp.toString()
        binding.monsterCreationCurrentSlots.text = slot.toString()
    }

    private fun updateUI(){
        var strength = 0
        var dext = 0
        var intel = 0
        var hp = 0
        var slot = 0
        for( i in partsListUsed){
            strength+=i.pStrength
            dext+=i.pDext
            intel+=i.pIntel
            hp+=i.pHP
            slot+=i.pSlot
        }
        binding.monsterCreationStatsStrengh.text = strength.toString()
        binding.monsterCreationStatsIntel.text = dext.toString()
        binding.monsterCreationStatsDext.text = intel.toString()
        binding.monsterCreationStatsHp.text = hp.toString()
        binding.monsterCreationCurrentSlots.text = slot.toString()

    }

    private fun loadParts() {
        partsRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in snapshot.children) {
                            availablePartsList.add(i.getValue(Parts::class.java)!!)
                        }
                        partsListUsed.add(availablePartsList[0])
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

    private fun createMonster(availablePartsList: ArrayList<Parts>, partsListUsed:  ArrayList<Parts>): Monster {
        val partsMonster: ArrayList<Int> = ArrayList()
        for (i in partsListUsed) {
            partsMonster.add(availablePartsList.indexOf(i))
        }
        //TODO small pb after typing name and interaction with adapter's buttons
        return Monster(binding.monsterCreationStatsHp.text.toString().toInt(), partsMonster,
                binding.monsterCreationMonsterName.text.toString(),
                binding.monsterCreationStatsStrengh.text.toString().toInt(),
                binding.monsterCreationStatsIntel.text.toString().toInt(),
                binding.monsterCreationStatsDext.text.toString().toInt())
    }

    private fun uploadMonster(availablePartsList: ArrayList<Parts>, partsListUsed:  ArrayList<Parts>) {
        // Monster handling
        val monster = createMonster(availablePartsList, partsListUsed)
        val monsterId = monsterRef.push().key.toString()
        monsterRef.child(monsterId).setValue(monster)

        // Adding monster to user's monster list
        //TODO prevent creating when slot above limite
        userRef.child(getUserId()).child("listMonsters").child(monsterId).setValue(monster)
    }
}