package com.example.tabatatimer.screens.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tabatatimer.R
import com.example.tabatatimer.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {

    lateinit var binding: FragmentTimerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTimerBinding.inflate(inflater)
        return binding.root
    }

}