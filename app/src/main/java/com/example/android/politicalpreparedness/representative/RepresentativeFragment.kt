package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
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
import kotlinx.android.synthetic.main.fragment_representative.*
import timber.log.Timber
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
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        if (savedInstanceState!=null){
            val motionLayoutStat: Int = savedInstanceState.getInt("motion_stat")
            binding.motionLayout.transitionToState(motionLayoutStat)
        }


        binding.viewModel = viewModel

        fusedLocationProviderClient = this.requireContext().let { LocationServices.getFusedLocationProviderClient(it) }

        representativesListAdapter= RepresentativeListAdapter()

        binding.representativesRecyclerView.adapter=representativesListAdapter

        viewModel.representatives.observe(viewLifecycleOwner, Observer { representatives ->
            representativesListAdapter.submitList(representatives)
            if(representatives.isNotEmpty()){
                savedInstanceState?.getInt("motion_stat")?.let{
                    binding.motionLayout.transitionToState(it)
                    Timber.i("Test :motionLayout")
                }
            }

        })

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            if (isOnline(requireContext())){
                viewModel.getRepresentatives()
            }else{
                Snackbar.make(requireView(), R.string.err_lost_connection,Snackbar.LENGTH_LONG).show()
            }

        }
        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            if (isOnline(requireContext())) {
                getRepresentativesInMyLocation()
            }else{
                Snackbar.make(requireView(), R.string.err_lost_connection,Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it==ApiStatus.ERROR){
                Snackbar.make(requireView(), R.string.error_representatives,Snackbar.LENGTH_LONG).show()
            }
        })

        savedInstanceState?.getParcelable<Address>("address")?.let{
            viewModel.getRepresentatives(it).let {
               Timber.i("Test :getRepresentatives >>>>>>>>>>>>>>>>>")
            }
        }



        return binding.root
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("address",binding.viewModel?.address?.value)
        outState.putInt("motion_stat",binding.motionLayout.currentState)
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

//    checking network state
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Timber.i("NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Timber.i( "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Timber.i("NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }



}