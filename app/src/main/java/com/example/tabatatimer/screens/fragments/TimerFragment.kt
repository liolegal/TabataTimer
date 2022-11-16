package com.example.tabatatimer.screens.fragments

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.navArgs
import com.example.tabatatimer.R
import com.example.tabatatimer.databinding.FragmentTimerBinding
import com.example.tabatatimer.services.TimerService
import com.example.tabatatimer.viewmodel.TimerViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabatatimer.Constants.TIMER_ACTION_TYPE
import com.example.tabatatimer.Constants.TIMER_BROADCAST_ACTION
import com.example.tabatatimer.Constants.TIMER_STARTED
import com.example.tabatatimer.Constants.TIMER_STOPPED
import com.example.tabatatimer.screens.adapters.PhasesAdapter
import com.example.tabatatimer.services.TimerPhase

class TimerFragment : Fragment() {

    private val viewModel: TimerViewModel by activityViewModels()

    lateinit var binding: FragmentTimerBinding
    private val args by navArgs<com.example.tabatatimer.screens.fragments.TimerFragmentArgs>()
    private var timerServiceConnection: ServiceConnection? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    private var timerService: TimerService? = null
    private var currentPhase = 0
    val adapter = PhasesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTimerBinding.inflate(inflater)
        binding.root.setBackgroundColor(args.currentSequence.color.toInt())
        (activity as AppCompatActivity).supportActionBar?.title = args.currentSequence.name
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setData(getPhases())
        startTimerService()
        binding.startBtn.setOnClickListener {
            timerService!!.start()
            binding.startBtn.isEnabled = false
            binding.pauseBtn.isEnabled = true
            binding.nextPhase.isEnabled=true
        }
        binding.pauseBtn.setOnClickListener {
            timerService!!.stop()
            binding.startBtn.isEnabled = true
            binding.pauseBtn.isEnabled = false
        }
        binding.nextPhase.setOnClickListener {
            timerService?.nextPhase()
            currentPhase++
            adapter.setSelectPhase(viewModel.currentPos.value!!)
        }
        setObservables()
        setBroadcastReceiver()
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
        service.currentPos.observe(activity as LifecycleOwner) {
            viewModel.currentPos.value = it
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
                    binding.startBtn.isEnabled = true
                    binding.pauseBtn.isEnabled = false
                    binding.nextPhase.isEnabled=false
                    adapter.setSelectPhase(0)
                    binding.textViewCountdown.text = args.currentSequence.warmUpTime.toString()
                    binding.cyclesTv.text = "1 / ${args.currentSequence.cycles}"
                }
            }

        }
        viewModel.currentPos.observe(activity as LifecycleOwner) {
            adapter.setSelectPhase(it)
            binding.recyclerView.scrollToPosition(it)
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
            this.setCyclesView((args.currentSequence.cycles + 1 - it).toString())
        }
    }

    private fun setBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.getStringExtra(TIMER_ACTION_TYPE)) {
                    TIMER_STARTED -> {
                        binding.startBtn.isEnabled = false
                        binding.pauseBtn.isEnabled = true
                    }
                    TIMER_STOPPED -> {
                        binding.startBtn.isEnabled = true
                        binding.pauseBtn.isEnabled = false
                    }
                }
            }
        }

        val filter = IntentFilter(TIMER_BROADCAST_ACTION)
        activity?.registerReceiver(broadcastReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (broadcastReceiver != null) {
            activity?.unregisterReceiver(broadcastReceiver)
        }

        if (timerServiceConnection != null) {
            activity?.unbindService(timerServiceConnection!!)
        }
        activity?.stopService(Intent(requireContext(), TimerService::class.java))
    }

    private fun setCyclesView(currentCycle: String) {
        binding.cyclesTv.text = "$currentCycle / ${args.currentSequence.cycles}"
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


