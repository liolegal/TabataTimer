package com.example.tabatatimer.screens.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tabatatimer.R
import com.example.tabatatimer.databinding.FragmentTimerBinding
import com.example.tabatatimer.services.TimerService
import com.example.tabatatimer.viewmodel.TimerViewModel
import android.content.ContextWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabatatimer.services.TimerPhase

import java.util.*

class TimerFragment : Fragment() {

    private val viewModel: TimerViewModel by activityViewModels()

    lateinit var binding: FragmentTimerBinding
    private val args by navArgs<TimerFragmentArgs>()
    private var timerServiceConnection: ServiceConnection? = null
    private var timerService: TimerService? = null
    private var currentPhase=0
    val adapter=PhasesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimerBinding.inflate(inflater)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setData(getPhases())
        startTimerService()
        binding.startBtn.setOnClickListener {
            binding.pauseBtn.visibility = View.VISIBLE
            binding.startBtn.visibility = View.INVISIBLE
            timerService!!.start()
        }
        binding.pauseBtn.setOnClickListener {
            binding.pauseBtn.visibility = View.INVISIBLE
            binding.startBtn.visibility = View.VISIBLE
            timerService!!.stop()
        }
        binding.nextPhase.setOnClickListener {
            timerService?.nextPhase()
            currentPhase++
            adapter.setSelectPhase(currentPhase)
        }
        setObservables()
        return binding.root
    }

    private fun startTimerService() { // in fact timer or sequence
        val intent = Intent(requireContext(), TimerService::class.java)
        activity?.startService(intent)

        timerServiceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(p0: ComponentName?) {}

            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                timerService = (binder as TimerService.TimerServiceBinder).getService()
                timerService!!.setTimer(args.currentSequence)
                bindServiceToViewModel(timerService!!)
            }
        }

        activity?.bindService(intent, timerServiceConnection!!, 0)
    }

    private fun bindServiceToViewModel(service: TimerService) {
        service.currentTimer.observe(activity as LifecycleOwner) {
            viewModel.currentTimer.value = it
        }

        service.currentPhase.observe(activity as LifecycleOwner) {
            viewModel.currentPhase.value = it
        }

        service.currentTimeRemaining.observe(activity as LifecycleOwner) {
            viewModel.currentTimeRemaining.value = it
        }
        service.workoutRemaining.observe(activity as LifecycleOwner) {
            viewModel.workoutRemaining.value = it
        }

        service.restRemaining.observe(activity as LifecycleOwner) {
            viewModel.restRemaining.value = it
        }

        service.cyclesRemaining.observe(activity as LifecycleOwner) {
            viewModel.cyclesRemaining.value = it
        }
        service.preparationRemaining.observe(activity as LifecycleOwner) {
            viewModel.preparationRemaining.value = it
        }
        service.currentPos.observe(activity as LifecycleOwner){
            viewModel.currentPos.value=it
        }

    }

    private fun setObservables() {
        viewModel.currentPhase.observe(activity as LifecycleOwner) {
            when (it) {
                TimerPhase.PREPARATION -> binding.timerType.text =
                    activity?.getString(R.string.warm_up_label)
                TimerPhase.WORKOUT -> binding.timerType.text =
                    activity?.getString(R.string.workout_label)
                TimerPhase.REST -> binding.timerType.text = activity?.getString(R.string.rest_label)
                TimerPhase.FINISHED -> {
                    binding.timerType.text = activity?.getString(R.string.finished_label)
                    binding.pauseBtn.visibility = View.INVISIBLE
                    binding.startBtn.visibility = View.VISIBLE
                    binding.textViewCountdown.text = args.currentSequence.warmUpTime.toString()
                }
            }

        }
//        viewModel.currentTimeRemaining.observe(activity as LifecycleOwner){
//            if (it >= 0) {
//                binding.textViewCountdown.text = it.toString()
//            }
//        }
        viewModel.currentPos.observe(activity as LifecycleOwner){
            adapter.setSelectPhase(it)
        }
        viewModel.preparationRemaining.observe(activity as LifecycleOwner) {
            if (it >= 0) {
                binding.textViewCountdown.text = (it).toString()
            }
        }

        viewModel.workoutRemaining.observe(activity as LifecycleOwner) {
            if (it >= 0) {
                binding.textViewCountdown.text = (it + 1).toString()
            }
        }

        viewModel.restRemaining.observe(activity as LifecycleOwner) {
            if (it >= 0) {
                binding.textViewCountdown.text = (it + 1).toString()
            }
        }

        viewModel.cyclesRemaining.observe(activity as LifecycleOwner) {
            // binding.textViewCountdown.text = it.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        activity?.stopService(Intent(requireContext(), TimerService::class.java))
    }

    private fun getPhases(): List<TimerPhase> {
        val phaseList = mutableListOf(TimerPhase.PREPARATION)
        for (index in 0 until args.currentSequence.cycles) {
            phaseList += TimerPhase.WORKOUT
            phaseList += TimerPhase.REST
        }
        return phaseList
    }

}

