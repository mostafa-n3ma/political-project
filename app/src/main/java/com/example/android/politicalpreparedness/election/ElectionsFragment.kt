package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.PoliticalApplication
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding

class ElectionsFragment : Fragment() {

    //    TODO: Declare ViewModel
    private val viewModel by viewModels<ElectionsViewModel>() {
        ElectionsViewModelFactory((requireContext().applicationContext as PoliticalApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentElectionBinding.inflate(inflater)
        //: Add ViewModel values and create ViewModel

        //: Add binding values

        //: Link elections to voter info

        //: Initiate recycler adapters

        //: Populate recycler adapters


        return binding.root
    }

//    TODO: Refresh adapters when fragment loads

}