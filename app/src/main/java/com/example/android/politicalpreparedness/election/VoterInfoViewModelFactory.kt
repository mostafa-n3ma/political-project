package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.source.ElectionsRepository

//TODO: Create Factory to generate VoterInfoViewModel with provided election datasource
@Suppress("UNCHECKED_CAST")
class VoterInfoViewModelFactory(
    private val electionsRepository: ElectionsRepository
)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>)=
        (VoterInfoViewModel(electionsRepository) as T)
}
