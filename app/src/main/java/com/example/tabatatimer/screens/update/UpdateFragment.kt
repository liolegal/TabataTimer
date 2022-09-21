package com.example.tabatatimer.screens.update

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tabatatimer.R
import com.example.tabatatimer.databinding.FragmentAddBinding
import com.example.tabatatimer.databinding.FragmentUpdateBinding
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import com.example.tabatatimer.viewmodel.BaseViewModel
import top.defaults.colorpicker.ColorPickerPopup

class UpdateFragment : Fragment() {
    lateinit var binding: FragmentAddBinding
    private lateinit var mBaseViewModel: BaseViewModel
    private val args by navArgs<UpdateFragmentArgs>()
    var backColor="000"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddBinding.inflate(inflater)
        mBaseViewModel= ViewModelProvider(this).get(BaseViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.title = activity?.getString(R.string.update)
        initNumberPickers()
        fillData(args.currentSequence)
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
                .showValue(true)
                .build()
                .show(view, object : ColorPickerPopup.ColorPickerObserver() {
                    override fun onColorPicked(color: Int) {
                        binding.root.setBackgroundColor(backColor.toInt())
                        binding.editTextName.backgroundTintList=ColorStateList.valueOf(backColor.toInt())
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
            warmUpSeconds.maxValue = 60
            workoutMinutes.maxValue = 30
            workoutSeconds.maxValue = 60
            workoutMinutes.minValue = 0
            workoutSeconds.minValue = 0
            restMinutes.minValue = 0
            restMinutes.maxValue = 30
            restSeconds.minValue = 0
            restSeconds.maxValue = 60
            cyclesNumberpicker.minValue=1
            cyclesNumberpicker.maxValue=10
        }


    }

    private fun insertToDatabase() {
        val warmupTime =(binding.warmUpMinutes.value*60+binding.warmUpSeconds.value).toLong()
        val workoutTime =(binding.workoutMinutes.value*60+binding.workoutSeconds.value).toLong()
        val restTime =(binding.restMinutes.value*60+binding.restSeconds.value ).toLong()
        val name = binding.editTextName.text.toString()
        val color = backColor
        val rounds = 1
        val cycles = binding.cyclesNumberpicker.value
        val sequence =
            SequenceDbEntity(args.currentSequence.id, name, color, warmupTime, workoutTime, restTime, rounds, cycles)
        mBaseViewModel.updateSequence(sequence)
        findNavController().navigateUp()
    }
    private fun fillData(item: SequenceDbEntity){

        binding.apply {
            backColor=item.color
            editTextName.setText(item.name)
            cyclesNumberpicker.value=item.cycles
            binding.root.setBackgroundColor(backColor.toInt())
            binding.editTextName.backgroundTintList=ColorStateList.valueOf(backColor.toInt())
            warmUpMinutes.value = (item.warmUpTime).toInt()/60
            warmUpSeconds.value=(item.warmUpTime).toInt()%60
            workoutMinutes.value =(item.workoutTime).toInt()/60
            workoutSeconds.value=(item.workoutTime).toInt()%60
            restMinutes.value = (item.restTime).toInt()/60
            restSeconds.value=(item.restTime).toInt()%60
        }
    }
}