package com.example.tabatatimer.screens.listOfSequences

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tabatatimer.R
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import com.example.tabatatimer.viewmodel.BaseViewModel

class ListAdapter(val mBaseViewModel: BaseViewModel) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
    private var sequenceList = emptyList<SequenceDbEntity>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.timer_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = sequenceList[position]
        //holder.itemView.findViewById<ConstraintLayout>(R.id.recycler_item).setBackgroundColor(currentItem.color.toInt())
        holder.itemView.findViewById<TextView>(R.id.title_of_sequence).text = currentItem.name
        holder.itemView.findViewById<ConstraintLayout>(R.id.timer_list_item).setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToTimerFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
        val popupMenu = androidx.appcompat.widget.PopupMenu(
            holder.itemView.context,
            holder.itemView.findViewById<ConstraintLayout>(R.id.timer_list_item)
        )
        popupMenu.inflate(R.menu.timer_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.update_btn -> {
                    val action =
                        ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
                    holder.itemView.findNavController().navigate(action)
                    true
                }
                R.id.delete_btn->{
                        val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
                        dialogBuilder.setMessage("Do you want to delete this sequence?")
                            .setCancelable(false)
                            .setPositiveButton(holder.itemView.context.getString(R.string.yes)) { _, _ ->
                                mBaseViewModel.deleteSequence(currentItem)
                            }
                            .setNegativeButton(holder.itemView.context.getString(R.string.no)) { dialog, _ ->
                                dialog.cancel()
                            }
                        val alert = dialogBuilder.create()
                        alert.setTitle(holder.itemView.context.getString(R.string.confirmation))
                        alert.show()
                        true

                }
                else -> false
            }
        }
        holder.itemView.findViewById<ConstraintLayout>(R.id.timer_list_item).setOnLongClickListener {
           popupMenu.show()
            true
        }

    }

    override fun getItemCount(): Int {
        return sequenceList.size
    }

    fun setData(sequence: List<SequenceDbEntity>) {
        this.sequenceList = sequence
        notifyDataSetChanged()
    }
}