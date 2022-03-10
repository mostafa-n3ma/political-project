package com.example.android.politicalpreparedness

import android.content.Context
import com.example.android.politicalpreparedness.data.source.DefaultLocaleDataSource
import com.example.android.politicalpreparedness.data.source.ElectionsRepository
import com.example.android.politicalpreparedness.data.source.LocaleDataSource.LocaleDataSource
import com.example.android.politicalpreparedness.data.source.RemoteDataSource.RemoteDataSource
import com.example.android.politicalpreparedness.data.source.database.ElectionDatabase

object ServiceLocator {
    private var electionDatabase:ElectionDatabase?=null

    @Volatile
    var electionsRepository:ElectionsRepository?=null



    fun provideRepository(context: Context):ElectionsRepository{
        synchronized(this){
            return electionsRepository?:createElectionRepository(context)
        }
    }

    private fun createElectionRepository(context: Context): ElectionsRepository {
        val newRepo=ElectionsRepository(RemoteDataSource,createLocaleDataSource(context))
        electionsRepository=newRepo
        return newRepo
    }

    private fun createLocaleDataSource(context: Context): DefaultLocaleDataSource {
        val database= electionDatabase?: ElectionDatabase.getInstance(context)
        return LocaleDataSource(database)
    }


}