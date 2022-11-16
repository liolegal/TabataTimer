package com.example.tabatatimer.screens.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tabatatimer.Constants
import com.example.tabatatimer.R
import com.example.tabatatimer.databinding.FragmentAddBinding
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import com.example.tabatatimer.viewmodel.BaseViewModel
import top.defaults.colorpicker.ColorPickerPopup


class AddFragment : Fragment() {
    lateinit var binding: FragmentAddBinding
    var backColor = "undefined"
    private lateinit var mBaseViewModel: BaseViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater)
        mBaseViewModel = ViewModelProvider(this).get(BaseViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.title = activity?.getString(R.string.add_sequence)
        initNumberPickers()
        mBaseViewModel.state.observe(activity as LifecycleOwner){
            if(it==Constants.UNIQUE_EXEPTION){
                Toast.makeText(context,"Name of timer is already exists",Toast.LENGTH_SHORT).show()
            }else if(it=="Added") {
                Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }

        }
        binding.saveBtn.setOnClickListener {
            if(checkInput()){
                insertToDatabase()
            }else Toast.makeText(context,"Fill in all the fields and set the color",Toast.LENGTH_SHORT).show()

        }
        binding.button.setOnClickListener {
            ColorPickerPopup.Builder(requireContext())
                .initialColor(R.color.teal_200)
                .enableBrightness(false)
                .okTitle(getString(R.string.choose))
                .cancelTitle(getString(R.string.cancel))
                .showIndicator(true)
                .build()
                .show(view, object : ColorPickerPopup.ColorPickerObserver() {
                    override fun onColorPicked(color: Int) {
                        backColor = color.toString()
                        binding.root.setBackgroundColor(backColor.toInt())
                        binding.editTextName.backgroundTintList= ColorStateList.valueOf(backColor.toInt())

                    }
                })
        }
    }

    private fun initNumberPickers() {
        binding.apply {
            warmUpMinutes.minValue = 0
            warmUpMinutes.maxValue = 30
            warmUpSeconds.minValue = 0
            warmUpSeconds.maxValue = 59
            workoutMinutes.maxValue = 30
            workoutSeconds.maxValue = 59
            workoutMinutes.minValue = 0
            workoutSeconds.minValue = 0
            restMinutes.minValue = 0
            restMinutes.maxValue = 59
            restSeconds.minValue = 0
            restSeconds.maxValue = 59
            cyclesNumberpicker.minValue=1
            cyclesNumberpicker.maxValue=10
        }


    }

    private fun insertToDatabase() {
        val warmupTime =(binding.warmUpMinutes.value*60+binding.warmUpSeconds.value).toLong()
        val workoutTime =(binding.workoutMinutes.value*60+binding.workoutSeconds.value).toLong()
        val restTime =(binding.restMinutes.value*60+binding.restSeconds.value ).toLong()
        val name = binding.editTextName.text.toString().dropLastWhile { it == ' '}
        val color = backColor
        val rounds = 1
        val cycles = binding.cyclesNumberpicker.value
        val sequence =
            SequenceDbEntity(0, name, color, warmupTime, workoutTime, restTime, rounds, cycles)
        mBaseViewModel.addSequence(sequence)

    }
    private fun checkInput():Boolean{
        if(binding.editTextName.text.toString()=="") return false
        if(backColor=="undefined") return false
        if(binding.workoutSeconds.value==0 && binding.workoutMinutes.value == 0)return false
        if(binding.restMinutes.value==0 && binding.restSeconds.value==0)return false

        return true
    }



}