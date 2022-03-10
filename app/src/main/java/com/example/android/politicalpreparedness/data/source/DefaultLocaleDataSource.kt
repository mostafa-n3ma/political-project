package com.example.android.politicalpreparedness.data.source

import com.example.android.politicalpreparedness.network.models.Election

interface DefaultLocaleDataSource {


    suspend fun getSavedElections(): List<Election>?

    suspend fun deleteElection(electionId: Int)

    suspend fun saveElection(election: Election)


}