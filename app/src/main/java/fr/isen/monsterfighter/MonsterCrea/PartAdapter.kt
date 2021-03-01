package fr.isen.monsterfighter.MonsterCrea

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.isen.monsterfighter.Model.Parts
import fr.isen.monsterfighter.databinding.NewPartCellBinding

//TODO Change file name and creat "party" package for crea and recap monsters

class PartAdapter (private val entries: MutableList<Parts>, private val availablePartsList: ArrayList<Parts>): RecyclerView.Adapter<PartAdapter.PartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartViewHolder {
        return PartViewHolder(NewPartCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PartViewHolder, position: Int) {

        holder.partname.text = entries[position].pName
        Picasso.get().load(entries[position].pImgUrl).into(holder.monstercreationimg)

        holder.delbutton.setOnClickListener{
            entries.removeAt(position)
            notifyDataSetChanged()
        }

        holder.monstercreationnext.setOnClickListener{
            //TODO Trash/20 but if it works it ain't stupid
            if(availablePartsList.size-1 > availablePartsList.indexOf(entries[position])){
                entries[position] = (availablePartsList[availablePartsList.indexOf(entries[position])+1])
            }
            else {
                entries[position] = availablePartsList[0]
            }
            notifyDataSetChanged()
        }

        holder.monstercreationprevious.setOnClickListener{
            if(0 < availablePartsList.indexOf(entries[position])){
                entries[position] = availablePartsList[availablePartsList.indexOf(entries[position])-1]
            }
            else {
                entries[position] = availablePartsList[availablePartsList.size-1]
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return entries.count()
    }

    class PartViewHolder(partBinding: NewPartCellBinding): RecyclerView.ViewHolder(partBinding.root){
        //TODO de la mise en page ds le xml
        val monstercreationimg: ImageView = partBinding.monsterCreationImg
        val delbutton: TextView = partBinding.delButton
        val monstercreationprevious: TextView = partBinding.monsterCreationPrevious
        val monstercreationnext: TextView = partBinding.monsterCreationNext
        val partname: TextView = partBinding.partName
    }

}