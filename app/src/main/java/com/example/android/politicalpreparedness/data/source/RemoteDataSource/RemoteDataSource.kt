package com.example.android.politicalpreparedness.data.source.RemoteDataSource

import com.example.android.politicalpreparedness.data.source.DefaultRemoteDataSource
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.*

object RemoteDataSource :DefaultRemoteDataSource{



    override suspend fun getUpcomingElections(): List<Election> {
        return CivicsApi.retrofitService.getElections().elections
    }

    override suspend fun getVoterInformation(
        electionId: Int,
        division: Division
    ): VoterInfoResponse {
        var address : String = division.country

        if (division.country.isNotBlank() && division.state.isNotBlank()) {
            address = division.country  + ", " + division.state
        }

        return CivicsApi.retrofitService.getVoterInformation(address, electionId.toLong(), false)
    }

    override suspend fun getRepresentatives(
        address: Address,
        includeOffices: Boolean,
        levels: String?,
        roles: String?
    ): RepresentativeResponse {
        return CivicsApi.retrofitService.getRepresentatives(address, includeOffices, levels, roles)
    }


}