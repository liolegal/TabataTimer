package com.example.tabatatimer.screens.update

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tabatatimer.R
import com.example.tabatatimer.databinding.FragmentUpdateBinding
import com.example.tabatatimer.model.room.entities.SequenceDbEntity
import com.example.tabatatimer.viewmodel.BaseViewModel
import top.defaults.colorpicker.ColorPickerPopup

class UpdateFragment : Fragment() {
    lateinit var binding: FragmentUpdateBinding
    private lateinit var mBaseViewModel: BaseViewModel
    private val args by navArgs<UpdateFragmentArgs>()
    var backColor="000"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentUpdateBinding.inflate(inflater)
        mBaseViewModel= ViewModelProvider(this).get(BaseViewModel::class.java)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initNumberPickers()
        fillData(args.currentSequence)
        binding.saveUpdateBtn.setOnClickListener {
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
                        view.setBackgroundColor(color)
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
        val cycles = binding.cyclesEt.text.toString().toInt()
        val sequence =
            SequenceDbEntity(args.currentSequence.id, name, color, warmupTime, workoutTime, restTime, rounds, cycles)
        mBaseViewModel.updateSequence(sequence)
        findNavController().navigate(R.id.action_updateFragment_to_listFragment)
    }


    fun convertToMillisec(min: Int, sec: Int): Long {
        var res: Long
        res = (min * 60000 + sec * 1000).toLong()
        return res

    }
//    private fun convertToMinutes(input:Long): Long {
//        return (input/60000)
//    }
//    private fun convertToSeconds(input:Long):Long{
//        return (input - convertToMinutes(input)*60000)/1000
//    }
    private fun fillData(item: SequenceDbEntity){

        binding.apply {
            view?.setBackgroundColor(item.color.toInt())
            backColor=item.color
            editTextName.setText(item.name)
            cyclesEt.setText(item.rounds.toString())
//            warmUpMinutes.value = convertToMinutes(item.warmUpTime).toInt()
//            warmUpSeconds.value=convertToSeconds(item.warmUpTime).toInt()
//            workoutMinutes.value = convertToMinutes(item.workoutTime).toInt()
//            workoutSeconds.value=convertToSeconds(item.workoutTime).toInt()
//            restMinutes.value = convertToMinutes(item.restTime).toInt()
//            restSeconds.value=convertToSeconds(item.restTime).toInt()
            warmUpMinutes.value = (item.warmUpTime).toInt()/60
            warmUpSeconds.value=(item.warmUpTime).toInt()%60
            workoutMinutes.value =(item.workoutTime).toInt()/60
            workoutSeconds.value=(item.workoutTime).toInt()%60
            restMinutes.value = (item.restTime).toInt()/60
            restSeconds.value=(item.restTime).toInt()%60
        }
    }
}