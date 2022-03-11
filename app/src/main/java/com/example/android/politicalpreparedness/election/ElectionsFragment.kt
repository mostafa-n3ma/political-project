package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.PoliticalApplication
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*

class ElectionsFragment : Fragment() {

    //    TODO: Declare ViewModel
    private val viewModel by viewModels<ElectionsViewModel>() {
        ElectionsViewModelFactory((requireContext().applicationContext as PoliticalApplication).repository)
    }
    private lateinit var savedElectionListAdapter: ElectionListAdapter
    private lateinit var upcomingElectionListAdapter: ElectionListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner=this
        binding.viewModel=viewModel


        upcomingElectionListAdapter= ElectionListAdapter(ElectionListener {
            Timber.i("Upcoming Election clicked.")
            viewModel.navigateToVoterInformation(it)
        })
        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer {
            it.apply {
                upcomingElectionListAdapter.submitList(it)
            }
        })
        binding.upcomingElectionsRecyclerView.adapter=upcomingElectionListAdapter


        savedElectionListAdapter= ElectionListAdapter(ElectionListener {
            Timber.i("Upcoming Election clicked.")
            viewModel.navigateToVoterInformation(it)
        })
        viewModel.savedElections.observe(viewLifecycleOwner, Observer {
            it.apply {
                savedElectionListAdapter.submitList(it)
            }
        })
        binding.savedElectionsRecyclerView.adapter=savedElectionListAdapter

        viewModel.status.observe(viewLifecycleOwner, Observer<ApiStatus> { apiStatus ->
            when(apiStatus) {
                ApiStatus.ERROR -> {
                    Snackbar.make(requireView(), R.string.error_upcoming_elections, Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        })




        viewModel.navigateToVoterInformation.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                navigateToVoterInformation(it)
                viewModel.navigateToVoterInformationCompleted()
            }
        })




        return binding.root
    }

    private fun navigateToVoterInformation(it: Election?) {
        Timber.i("Election selected. Name: %s Division: %s", it?.name, it?.division)
        this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it!!.id, it!!.division))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refreshElections()
    }

}