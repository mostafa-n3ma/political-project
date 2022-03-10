package com.example.android.politicalpreparedness.data.source

import com.example.android.politicalpreparedness.network.models.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The repository class containing operations for use with both network and database operations.
 */
class ElectionsRepository(
    private val remoteDataSource: DefaultRemoteDataSource,
    private val localeDataSource: DefaultLocaleDataSource,
    private val dispatcher: CoroutineDispatcher=Dispatchers.IO
    ) {



    //Remote
     suspend fun getUpcomingElections(): List<Election> {
        return withContext(dispatcher) {
            remoteDataSource.getUpcomingElections()
        }
    }

     suspend fun getVoterInformation(
        electionId: Int,
        division: Division
    ): VoterInfoResponse {
        return withContext(dispatcher) {
             remoteDataSource.getVoterInformation(electionId, division)
        }
    }

     suspend fun getRepresentatives(
        address: Address,
        includeOffices: Boolean,
        levels: String?,
        roles: String?
    ): RepresentativeResponse {
        return withContext(dispatcher) {
            remoteDataSource.getRepresentatives(address, includeOffices, levels, roles)
        }
    }



    //Locale
     suspend fun getSavedElections(): List<Election>? {
        return withContext(dispatcher) {
            localeDataSource.getSavedElections()
        }
    }

     suspend fun deleteElection(electionId: Int) {
        withContext(dispatcher) {
            localeDataSource.deleteElection(electionId)
        }
    }

     suspend fun saveElection(election: Election) {
        withContext(dispatcher) {
            localeDataSource.saveElection(election)
        }
    }


}