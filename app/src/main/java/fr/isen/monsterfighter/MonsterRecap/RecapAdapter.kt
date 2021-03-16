package fr.isen.monsterfighter.MonsterRecap

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import fr.isen.monsterfighter.Model.Monster
import fr.isen.monsterfighter.Model.Parts
import fr.isen.monsterfighter.R
import fr.isen.monsterfighter.RegisterActivity
import fr.isen.monsterfighter.databinding.RecapCellBinding
import fr.isen.monsterfighter.utils.FirebaseUtils
import fr.isen.monsterfighter.utils.FirebaseUtils.userRef


class RecapAdapter (private val entries: MutableList<Monster>, private val availablePartsList: ArrayList<Parts>, parentContext: Context): RecyclerView.Adapter<RecapAdapter.RecapViewHolder>() {

    private var context = parentContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecapViewHolder {
        return RecapViewHolder(RecapCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecapViewHolder, position: Int) {

        var key = ""

        userRef.child(getUserId()).child("listMonsters").get().addOnSuccessListener {
            it.children.firstOrNull { it.getValue(Monster::class.java) == entries[position] }?.let {
                it.key?.let {
                    key = it
                }
            }
        }

        Picasso.get().load(availablePartsList[entries[position].mlstPartsId[0]].pImgUrl).into(holder.monsterimg)
        holder.monstername.text = entries[position].mname


        holder.delbutton.setOnClickListener{
            userRef.child(getUserId()).child("selectedMonsters").get().addOnSuccessListener {
                var monsterID = it.value.toString()
                monsterID?.let {
                    if(it==key){
                        userRef.child(getUserId()).child("selectedMonsters").removeValue()
                    }

                    entries.removeAt(position)
                    userRef.child(getUserId()).child("listMonsters").child(key).removeValue()
                    }
                }
        }

        holder.monsterswap.setOnClickListener{
            userRef.child(getUserId()).child("selectedMonsters").setValue(key)
        }

        holder.monsterdetail.setOnClickListener{
            //TODO detail button
            //intent
            //finish()
        }
    }

    override fun getItemCount(): Int {
        return entries.count()
    }

    class RecapViewHolder(recapBinding: RecapCellBinding): RecyclerView.ViewHolder(recapBinding.root){
        //TODO de la mise en page ds le xml
        val monsterimg: ImageView = recapBinding.monsterRecapImg
        val delbutton: TextView = recapBinding.monsterDelete
        val monsterswap: TextView = recapBinding.monsterSwap
        val monsterdetail: TextView = recapBinding.monsterDetail
        val monstername: TextView = recapBinding.monsterName
        var detailbackground: CardView = recapBinding.detailBackground
    }

    private fun getUserId(): String {
        // accessing cache
        val sharedPreferences = context.getSharedPreferences(RegisterActivity.USER_PREF,
            AppCompatActivity.MODE_PRIVATE
        )
        return sharedPreferences.getString(RegisterActivity.USER_ID, "")!!
    }
}