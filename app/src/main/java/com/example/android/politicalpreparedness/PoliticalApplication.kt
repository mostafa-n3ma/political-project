package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.data.source.ElectionsRepository
import timber.log.Timber

class PoliticalApplication : Application() {
    val repository: ElectionsRepository
        get() = ServiceLocator.provideRepository(this)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}