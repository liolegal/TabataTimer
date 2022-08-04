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
import com.example.tabatatimer.viewmodel.SequenceViewModel

class ListFragment : Fragment() {
    lateinit var binding:FragmentListBinding
    private lateinit var mSequenceViewModel: SequenceViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentListBinding.inflate(inflater)
        val adapter=ListAdapter()
        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        mSequenceViewModel=ViewModelProvider(this).get(SequenceViewModel::class.java)
        mSequenceViewModel.readAllData.observe(viewLifecycleOwner,Observer{ sequence ->
            adapter.setData(sequence)
        })
        return binding.root
    }

    companion object {

        @JvmStatic fun newInstance()=ListFragment()
    }
}