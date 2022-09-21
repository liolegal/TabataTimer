package com.example.tabatatimer.screens.add

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tabatatimer.R
import com.example.tabatatimer.databinding.FragmentAddBinding
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import com.example.tabatatimer.viewmodel.BaseViewModel
import top.defaults.colorpicker.ColorPickerPopup


class AddFragment : Fragment() {
    lateinit var binding: FragmentAddBinding
    var backColor = "000"
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
        binding.saveBtn.setOnClickListener {
            insertToDatabase()
        }
        binding.button.setOnClickListener {
            ColorPickerPopup.Builder(requireContext())
                .initialColor(Color.WHITE)
                .enableBrightness(false)
                .okTitle(getString(R.string.choose))
                .cancelTitle(getString(R.string.cancel))
                .showIndicator(true)
                .showValue(false)
                .build()
                .show(view, object : ColorPickerPopup.ColorPickerObserver() {
                    override fun onColorPicked(color: Int) {
                        binding.root.setBackgroundColor(backColor.toInt())
                        binding.editTextName.backgroundTintList= ColorStateList.valueOf(backColor.toInt())
                        backColor = color.toString()
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
            //convertToMillisec(binding.warmUpMinutes.value, binding.warmUpSeconds.value)
        val workoutTime =(binding.workoutMinutes.value*60+binding.workoutSeconds.value).toLong()
            //convertToMillisec(binding.workoutMinutes.value, binding.workoutSeconds.value)
        val restTime =(binding.restMinutes.value*60+binding.restSeconds.value ).toLong()
            //convertToMillisec(binding.restMinutes.value, binding.restSeconds.value)
        val name = binding.editTextName.text.toString()
        val color = backColor
        val rounds = 1
        val cycles = binding.cyclesNumberpicker.value
        val sequence =
            SequenceDbEntity(0, name, color, warmupTime, workoutTime, restTime, rounds, cycles)
        mBaseViewModel.addSequence(sequence)
        findNavController().navigate(R.id.action_addFragment_to_listFragment)
    }



}