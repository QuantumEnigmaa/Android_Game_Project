package fr.isen.monsterfighter

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import fr.isen.monsterfighter.Extensions.Extensions.dialog
import fr.isen.monsterfighter.Model.Monster
import fr.isen.monsterfighter.Model.Parts
import fr.isen.monsterfighter.databinding.ActivityMonsterCreationBinding
//import fr.isen.monsterfighter.utils.FirebaseUtils.getUserId
import fr.isen.monsterfighter.utils.FirebaseUtils.monsterRef
import fr.isen.monsterfighter.utils.FirebaseUtils.partsRef
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef

class MonsterCreationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMonsterCreationBinding

    private lateinit var partsList: ArrayList<Parts>
    private lateinit var images: ArrayList<ImageView>
    private lateinit var imagePositionList: ArrayList<Int>

    companion object {
        private const val MAX_SLOTS = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonsterCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetching monster parts data from Firebase
        images = createListImages()
        imagePositionList = ArrayList(4)
        Log.i("imagePositionList", imagePositionList.toString())
        partsList = loadParts(images)

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
        binding.monsterCreationNext1.setOnClickListener(this)
        binding.monsterCreationNext2.setOnClickListener(this)
        binding.monsterCreationNext3.setOnClickListener(this)
        binding.monsterCreationNext4.setOnClickListener(this)
        binding.monsterCreationPrevious1.setOnClickListener(this)
        binding.monsterCreationPrevious2.setOnClickListener(this)
        binding.monsterCreationPrevious3.setOnClickListener(this)
        binding.monsterCreationPrevious4.setOnClickListener(this)
        binding.monsterCreationSave.setOnClickListener(this)
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

    private fun createListImages(): ArrayList<ImageView> {
        val listImages = ArrayList<ImageView>(4)
        listImages.add(binding.MonsterCreationPart1)
        listImages.add(binding.monsterCreationPart2)
        listImages.add(binding.monsterCreationPart3)
        listImages.add(binding.monsterCreationPart4)
        return listImages
    }

    private fun initUI(listParts: ArrayList<Parts>, listImage: ArrayList<ImageView>) {
        var i = 0
        var strength = 0
        var dext = 0
        var intel = 0
        var hp = 0
        var slot = 0
        while (i < listImage.size) {
            Picasso.get().load(listParts[i].pImgUrl).placeholder(R.drawable.searching).into(listImage[i])
            imagePositionList.add(i)
            strength += listParts[i].pStrength
            dext += listParts[i].pDext
            intel += listParts[i].pIntel
            hp += listParts[i].pHP
            slot += listParts[i].pSlot
            i++
        }
        binding.monsterCreationStatsStrengh.text = strength.toString()
        binding.monsterCreationStatsIntel.text = dext.toString()
        binding.monsterCreationStatsDext.text = intel.toString()
        binding.monsterCreationStatsHp.text = hp.toString()
        binding.monsterCreationCurrentSlots.text = slot.toString()
    }

    private fun loadParts(images: ArrayList<ImageView>): ArrayList<Parts> {
        val lst: ArrayList<Parts> = ArrayList()
        partsRef.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in snapshot.children) {
                            lst.add(i.getValue(Parts::class.java)!!)
                        }
                        initUI(lst, images)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i("parts loading error", error.toString())
                    }

                }
        )
        return lst
    }

    override fun onClick(v: View?) {
        when (v) {
            //TODO ADD METHODS TO SWITCH IMAGES
            binding.monsterCreationNext1 -> { changeImage(0, true) }
            binding.monsterCreationNext2 -> { changeImage(1, true) }
            binding.monsterCreationNext3 -> { changeImage(2, true) }
            binding.monsterCreationNext4 -> { changeImage(3, true) }
            binding.monsterCreationPrevious1 -> { changeImage(0, false) }
            binding.monsterCreationPrevious2 -> { changeImage(1, false) }
            binding.monsterCreationPrevious3 -> { changeImage(2, false) }
            binding.monsterCreationPrevious4 -> { changeImage(3, false) }
            binding.monsterCreationSave -> {
                val slots: String = binding.monsterCreationCurrentSlots.text.toString()
                if (slots.toInt() <= MAX_SLOTS && binding.monsterCreationMonsterName.text.toString() != "") {
                    uploadMonster(partsList)
                    startActivity(Intent(this, MonsterRecapActivity::class.java))
                    finish()
                } else {
                    if (slots.toInt() > MAX_SLOTS)
                        dialog("Nombre maximum d'emplacement dépassé !", "Attention !", true) {}
                    else
                        dialog("Donnez un nom à votre monstre !", "Attention !", true) {}
                }
            }
        }
    }

    private fun changeImage(i: Int, next: Boolean) {
        // Saving current stats values
        val currentStrength = partsList[imagePositionList[i]].pStrength
        val currentIntel = partsList[imagePositionList[i]].pIntel
        val currentDext = partsList[imagePositionList[i]].pDext
        val currentHP = partsList[imagePositionList[i]].pHP
        val currentSlot = partsList[imagePositionList[i]].pSlot

        // Updating Part position
        if (imagePositionList[i] == (partsList.size - 1) && next) {
            imagePositionList[i] = 0
        } else if (imagePositionList[i] == 0 && !next) {
            imagePositionList[i] = partsList.size - 1
        } else {
            if (next) imagePositionList[i] ++ else imagePositionList[i] --
        }
        Picasso.get().load(partsList[imagePositionList[i]].pImgUrl).into(images[i])

        // Updating stats values
        val newStrengthValue = binding.monsterCreationStatsStrengh.text.toString().toInt() - currentStrength + partsList[imagePositionList[i]].pStrength
        val newIntelValue = binding.monsterCreationStatsIntel.text.toString().toInt() - currentIntel + partsList[imagePositionList[i]].pIntel
        val newDextValue = binding.monsterCreationStatsDext.text.toString().toInt() - currentDext + partsList[imagePositionList[i]].pDext
        val newHpValue = binding.monsterCreationStatsHp.text.toString().toInt() - currentHP + partsList[imagePositionList[i]].pHP
        val newSlotValue = binding.monsterCreationCurrentSlots.text.toString().toInt() - currentSlot + partsList[imagePositionList[i]].pSlot
        binding.monsterCreationStatsStrengh.text  = newStrengthValue.toString()
        binding.monsterCreationStatsIntel.text = newIntelValue.toString()
        binding.monsterCreationStatsDext.text = newDextValue.toString()
        binding.monsterCreationStatsHp.text = newHpValue.toString()
        binding.monsterCreationCurrentSlots.text = newSlotValue.toString()
    }

    fun getUserId(): String {
        // accessing cache
        val sharedPreferences = getSharedPreferences(RegisterActivity.USER_PREF, MODE_PRIVATE)
        return sharedPreferences.getString(RegisterActivity.USER_ID, "")!!
    }

    private fun createMonster(lstTotParts: ArrayList<Parts>): Monster {
        //TODO TRANSFORM PARTSMONSTER INTO AN ID LIST INSTEAD OF A PARTS LIST FOR MORE EFFICIENT DATABASE USAGE
        val partsMonster: ArrayList<Parts> = ArrayList(4)
        var i = 0
        while (i < 4) {
            partsMonster.add(lstTotParts[i])
            i++
        }
        return Monster(binding.monsterCreationStatsHp.text.toString().toInt(), partsMonster,
                binding.monsterCreationMonsterName.text.toString(),
                binding.monsterCreationStatsStrengh.text.toString().toInt(),
                binding.monsterCreationStatsIntel.text.toString().toInt(),
                binding.monsterCreationStatsDext.text.toString().toInt())
    }

    private fun uploadMonster(partsList: ArrayList<Parts>) {
        // Monster handling
        val monster = createMonster(partsList)
        val monsterId = monsterRef.push().key.toString()
        monsterRef.child(monsterId).setValue(monster)

        // Adding monster to user's monster list
        val monsterList: ArrayList<Monster> = ArrayList()
        monsterList.add(monster)
        userRef.child(getUserId()).child("listMonsters").setValue(monsterList)
    }
}