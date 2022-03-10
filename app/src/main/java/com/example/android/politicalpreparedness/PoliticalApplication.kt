package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.data.source.ElectionsRepository

class PoliticalApplication : Application() {
    val repository: ElectionsRepository
        get() = ServiceLocator.provideRepository(this)
}