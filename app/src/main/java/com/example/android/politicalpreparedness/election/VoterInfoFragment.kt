package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.PoliticalApplication
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Division

class VoterInfoFragment : Fragment() {

    private val viewModel by viewModels<VoterInfoViewModel>() {
        VoterInfoViewModelFactory((requireContext().applicationContext as PoliticalApplication).repository)
    }



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val voterInfoFragmentArgs = VoterInfoFragmentArgs.fromBundle(arguments!!)
        val electionId : Int = voterInfoFragmentArgs.argElectionId
        val division : Division = voterInfoFragmentArgs.argDivision


        // Check for required arguments
        if (division.country.isBlank() || division.state.isBlank()) {
            displayInvalidRequestErrorDialog()
        } else {

            // Retrieve the voter information
            viewModel.getVoterInformation(electionId, division)
            viewModel.url.observe(viewLifecycleOwner, Observer {url ->
                if (url!=null){
                    loadUrl(url)
                }
            })
        }
        return binding.root
    }

    private fun displayInvalidRequestErrorDialog() {
        AlertDialog.Builder(context!!)
            .setTitle(R.string.error_message_title)
            .setCancelable(false)
            .setMessage(R.string.error_message_invalid_election_request)
            .setPositiveButton("Ok") { dialog, _ ->
                // Dismiss the dialog and return to the previous screen
                dialog.dismiss()
                this.findNavController().popBackStack()
            }.show()
    }


    private fun loadUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
        viewModel.navigateToUrlCompleted()
    }
}

