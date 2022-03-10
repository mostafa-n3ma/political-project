package com.example.android.politicalpreparedness.data.source

import com.example.android.politicalpreparedness.network.models.*

interface DefaultRemoteDataSource {

    suspend fun getUpcomingElections(): List<Election>
    suspend fun getVoterInformation(electionId : Int, division: Division): VoterInfoResponse

    suspend fun getRepresentatives(address: Address, includeOffices: Boolean, levels: String?, roles: String?): RepresentativeResponse
    }