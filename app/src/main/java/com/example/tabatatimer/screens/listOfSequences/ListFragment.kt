package com.example.tabatatimer.screens.listOfSequences

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabatatimer.databinding.FragmentListBinding
import com.example.tabatatimer.viewmodel.BaseViewModel

class ListFragment : Fragment() {
    lateinit var binding: FragmentListBinding
    private lateinit var mBaseViewModel: BaseViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater)
        mBaseViewModel = ViewModelProvider(this).get(BaseViewModel::class.java)

        val adapter = ListAdapter(mBaseViewModel)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mBaseViewModel.readAllData.observe(viewLifecycleOwner, Observer { sequence ->
            adapter.setData(sequence)
        })
        return binding.root
    }

}