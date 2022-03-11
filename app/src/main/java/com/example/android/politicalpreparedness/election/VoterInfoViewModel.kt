package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.source.ElectionsRepository
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(private val electionsRepository: ElectionsRepository) : ViewModel() {

    //:  live data to hold voter info
    private val _voterInfo=MutableLiveData<Election>()
    val voterInfo:LiveData<Election> get() = _voterInfo

    // Live Data to hold voter information's administration body
    private val _electionAdministrationBody = MutableLiveData<AdministrationBody>()
    val electionAdministrationBody: LiveData<AdministrationBody>
        get() = _electionAdministrationBody

    // Live Data to hold voter information's address
    private val _correspondenceAddress = MutableLiveData<Address>()
    val correspondenceAddress: LiveData<Address>
        get() = _correspondenceAddress




    // Live Data to hold whether the election has been saved
    private val _isSaved = MutableLiveData<Boolean>()
    val isElectionSaved: LiveData<Boolean>
        get() = _isSaved



    // Live Data to hold the selected URL
    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url


    // Live Data that stores the status of the most recent voter information request
    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status



    /**
     * Retrieves voter information.
     */
    fun getVoterInformation(electionId: Int, division: Division) {
        viewModelScope.launch {
            try {
                _status.value = ApiStatus.LOADING
                // Check if this is a saved election first (this will help set up state for the button)
                val savedElection : Election? = electionsRepository.getElection(electionId)
                Timber.i("Election already saved? %s", (savedElection != null))
                _isSaved.value = savedElection != null

                // Get voter information
                val voterInformationResponse = electionsRepository.getVoterInformation(electionId, division)

                // Set individual values
                _voterInfo.value = voterInformationResponse.election
                _electionAdministrationBody.value = voterInformationResponse.state?.first()?.electionAdministrationBody
                _correspondenceAddress.value = voterInformationResponse.state?.first()?.electionAdministrationBody?.correspondenceAddress
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Timber.e(e, "Could not retrieve voter information...")
                _status.value = ApiStatus.ERROR
                clear()
            }
        }
    }




    /**
     * Helper method to clear election data.
     */
    private fun clear() {
        _voterInfo.value = null
        _electionAdministrationBody.value = null
        _correspondenceAddress.value = null
    }





    /**
     * Follows or unfollows an [Election].
     */
    fun followOrUnfollowElection() {
        viewModelScope.launch {
            _voterInfo.value?.let {
                if (_isSaved.value == true) {
                    // Delete the Election and set the flag to false
                    electionsRepository.deleteElection(it.id)
                    _isSaved.value = false
                } else {
                    // Save the Election and set the flag to true
                    electionsRepository.saveElection(it)
                    _isSaved.value = true
                }
            }
        }
    }






    /**
     * Sets the selected URL and navigates to the website.
     */
    fun navigateToUrl(url : String) {
        Timber.i("Url clicked is: %s", url)
        _url.value = url
    }






    /**
     * Clears the selected URL after navigating to the website.
     */
    fun navigateToUrlCompleted() {
        _url.value = null
    }


}



//TODO: Add var and methods to populate voter info

//TODO: Add var and methods to support loading URLs

//TODO: Add var and methods to save and remove elections to local database
//TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

/**
 * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
 */