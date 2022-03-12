package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.PoliticalApplication
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class RepresentativeFragment : Fragment() {

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 94
    }

    private val viewModel by viewModels<RepresentativeViewModel> {
        RepresentativeViewModelFactory((requireContext().applicationContext as PoliticalApplication).repository)
    }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var representativesListAdapter: RepresentativeListAdapter
    private lateinit var binding: FragmentRepresentativeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        fusedLocationProviderClient = this.requireContext().let { LocationServices.getFusedLocationProviderClient(it) }

        representativesListAdapter= RepresentativeListAdapter()

        binding.representativesRecyclerView.adapter=representativesListAdapter

        viewModel.representatives.observe(viewLifecycleOwner, Observer { representatives ->
            representativesListAdapter.submitList(representatives)
        })

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.getRepresentatives()
        }
        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            getRepresentativesInMyLocation()
        }

        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it==ApiStatus.ERROR){
                Snackbar.make(requireView(), R.string.error_representatives,Snackbar.LENGTH_LONG).show()
            }

        })


        return binding.root

    }


    private fun getRepresentativesInMyLocation() {
        return if (isPermissionGranted()) {
            getLocation()
        }  else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            }
        }



    private fun isPermissionGranted() : Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        // Use Location Service to obtain a FusedLocationProviderClient
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                viewModel.getRepresentatives(geoCodeLocation(location))
            }
        }

    }


    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
            }
            .first()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if the request code was the same code we used
        if (PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User has granted permissions, so request the location
                getLocation()
            } else {
                // User has denied permissions so show a snackbar message
                Snackbar.make(requireView(), R.string.error_location_services_required, Snackbar.LENGTH_LONG).show()
            }
        }
    }


    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}