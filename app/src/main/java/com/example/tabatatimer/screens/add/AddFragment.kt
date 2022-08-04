package com.example.tabatatimer.screens.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tabatatimer.R
import com.example.tabatatimer.databinding.FragmentAddBinding
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import com.example.tabatatimer.viewmodel.SequenceViewModel

public class AddFragment : Fragment() {
    lateinit var binding: FragmentAddBinding
private lateinit var mSequenceViewModel: SequenceViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater)
        mSequenceViewModel=ViewModelProvider(this).get(SequenceViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        binding.numberPickerMinutes.minValue=0
//        binding.numberPickerMinutes.maxValue=30
//        binding.textView.setOnClickListener{
//            binding.textView.text=binding.numberPickerMinutes.value.toString()
//        }
        initNumberPickers()
        binding.button.setOnClickListener {

           insertToDatabase()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = AddFragment()
    }

    private fun initNumberPickers() {
        binding.apply {
            warmUpMinutes.minValue = 0
            warmUpMinutes.maxValue = 30
            warmUpSeconds.minValue = 0
            warmUpSeconds.maxValue = 60
            workoutMinutes.maxValue = 30
            workoutSeconds.maxValue = 60
            workoutMinutes.minValue = 0
            workoutSeconds.minValue = 0
            restMinutes.minValue = 0
            restMinutes.maxValue = 30
            restSeconds.minValue = 0
            restSeconds.maxValue = 60
        }


    }
    private fun insertToDatabase(){
        val warmupTime=convertToMillisec(binding.warmUpMinutes.value,binding.warmUpSeconds.value)
        val workoutTime=convertToMillisec(binding.workoutMinutes.value,binding.workoutSeconds.value)
        val restTime=convertToMillisec(binding.restMinutes.value,binding.restSeconds.value)
        val name=binding.editTextName.text.toString()
        val color="blue"
        val rounds=binding.roundsEt.text.toString().toInt()
        val cycles=binding.cyclesEt.text.toString().toInt()
        val sequence=SequenceDbEntity(0,name,color,warmupTime,workoutTime,restTime,rounds,cycles)
        mSequenceViewModel.addSequence(sequence)
        findNavController().navigate(R.id.action_addFragment_to_listFragment)
    }


    fun convertToMillisec(min: Int, sec: Int): Long {
        var res: Long = 0L
        res = (min * 60000 + sec * 1000).toLong()
        return res

    }


}