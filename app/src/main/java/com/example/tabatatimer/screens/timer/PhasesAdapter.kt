package com.example.tabatatimer.screens.timer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.tabatatimer.R
import com.example.tabatatimer.services.TimerPhase
import com.example.tabatatimer.viewmodel.BaseViewModel

class PhasesAdapter() : RecyclerView.Adapter<PhasesAdapter.MyViewHolder>() {
    private var phaseList = emptyList<TimerPhase>()
    private var selectPhase=0

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.phase_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = phaseList[position]

        //holder.itemView.findViewById<ConstraintLayout>(R.id.recycler_item).setBackgroundColor(currentItem.color.toInt())
        if(selectPhase==position){//#30676767holder.itemView.context.getColor(R.color.selectionColor)
            holder.itemView.findViewById<ConstraintLayout>(R.id.phase_list_item).setBackgroundColor(("#FF018786".toColorInt()))
        }else holder.itemView.findViewById<ConstraintLayout>(R.id.phase_list_item).setBackgroundColor(("#FFFFFFFF".toColorInt()))
        when(currentItem){
            TimerPhase.PREPARATION->holder.itemView.findViewById<TextView>(R.id.title_of_phase).text=
                holder.itemView.context.getString(R.string.warm_up_label)
            TimerPhase.WORKOUT->holder.itemView.findViewById<TextView>(R.id.title_of_phase).text=
                holder.itemView.context.getString(R.string.workout_label)
            TimerPhase.REST->holder.itemView.findViewById<TextView>(R.id.title_of_phase).text=
                holder.itemView.context.getString(R.string.rest_label)
            else -> {}
        }


    }

    override fun getItemCount(): Int {
        return phaseList.size
    }
    fun setData(phaseList: List<TimerPhase>){
        this.phaseList=phaseList
        notifyDataSetChanged()
    }
    fun setSelectPhase(position: Int){
        this.selectPhase=position
        notifyDataSetChanged()
    }

}