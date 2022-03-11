package com.example.android.politicalpreparedness.data.source.LocaleDataSource

import com.example.android.politicalpreparedness.data.source.DefaultLocaleDataSource
import com.example.android.politicalpreparedness.data.source.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election

class LocaleDataSource(private val database:ElectionDatabase):DefaultLocaleDataSource {


    override suspend fun getSavedElections(): List<Election>? {
        return  database.electionDao.getAllElections()
    }

    override suspend fun getElection(id: Int): Election? {
        return database.electionDao.getElection(id)
    }


    override suspend fun deleteElection(electionId: Int) {
        database.electionDao.deleteElection(electionId)
    }

    override suspend fun saveElection(election: Election) {
        database.electionDao.insert(election)
    }


}