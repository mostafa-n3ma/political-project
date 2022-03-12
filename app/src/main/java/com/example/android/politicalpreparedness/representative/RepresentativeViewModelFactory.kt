package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.source.ElectionsRepository

@Suppress("UNCHECKED_CAST")
class RepresentativeViewModelFactory(
    private val electionsRepository: ElectionsRepository
)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>)=
        (RepresentativeViewModel(electionsRepository) as T)
}
