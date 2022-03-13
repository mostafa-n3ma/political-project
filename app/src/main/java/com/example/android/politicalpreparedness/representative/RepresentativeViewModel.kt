package com.example.android.politicalpreparedness.representative

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.PoliticalApplication
import com.example.android.politicalpreparedness.data.source.ElectionsRepository
import com.example.android.politicalpreparedness.network.ApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(private val repository: ElectionsRepository): ViewModel() {

    // Live Data to hold the list of representatives
    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    // Live Data to hold the address
    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    // Live Data that stores the status of the most recent representative request
    private val _status: MutableLiveData<ApiStatus> = MutableLiveData()
    val status: LiveData<ApiStatus>
        get() = _status

    init {
        _address.value = Address("","","","","")
    }


    fun getRepresentatives(address: Address?) {
        viewModelScope.launch {
            _representatives.value = arrayListOf()
            if (address != null && address.isValidAddress()) {
                try {
                    _status.value = ApiStatus.LOADING
                    _address.value = address
                    val (offices, officials) = repository.getRepresentatives(address, true, null, null)
                    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
                    Timber.i("Number of representatives: ${representatives.value?.size}")
                    _status.value = ApiStatus.DONE
                } catch (e: Exception) {
                    Timber.e(e, "Could not retrieve the representatives.")
                    _status.value = ApiStatus.ERROR
                }
            }
        }
    }


    fun getRepresentatives() {
            getRepresentatives(_address.value)
    }


}







