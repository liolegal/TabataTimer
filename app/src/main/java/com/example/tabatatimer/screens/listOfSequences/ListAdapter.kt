package com.example.tabatatimer.screens.listOfSequences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabatatimer.R
import com.example.tabatatimer.model.room.entities.SequenceDbEntity

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>(){
    private var sequenceList= emptyList<SequenceDbEntity>()
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=sequenceList[position]
        holder.itemView.findViewById<TextView>(R.id.title_of_sequence).text=currentItem.name
    }

    override fun getItemCount(): Int {
        return sequenceList.size
    }

    fun setData(sequence:List<SequenceDbEntity>){
        this.sequenceList=sequence
        notifyDataSetChanged()
    }
}