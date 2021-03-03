package fr.isen.monsterfighter.MonsterRecap

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.monsterfighter.Model.Monster
import fr.isen.monsterfighter.Model.Parts
import fr.isen.monsterfighter.databinding.RecapCellBinding


class RecapAdapter (private val entries: ArrayList<Monster>, private val availablePartsList: ArrayList<Parts>): RecyclerView.Adapter<RecapAdapter.RecapViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecapViewHolder {
        return RecapViewHolder(RecapCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecapViewHolder, position: Int) {

        //TODO Color For used monster (change with sav and need to upload to user profil)

        Picasso.get().load(availablePartsList[entries[position].mLstPartsId[0]].pImgUrl).into(holder.monsterimg)
        holder.monstername.text = entries[position].mName


        holder.delbutton.setOnClickListener{
            entries.removeAt(position)
            notifyDataSetChanged()
        }

        holder.monsterswap.setOnClickListener{
            //TODO swap button
        }

        holder.monsterdetail.setOnClickListener{
            //TODO detail button
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
    }
}