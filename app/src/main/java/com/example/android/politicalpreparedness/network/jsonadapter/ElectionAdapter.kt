package com.example.android.politicalpreparedness.network.jsonadapter

import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class ElectionAdapter {
    @FromJson
    fun divisionFromJson (ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val anyDelimiter=""
        val country = ocdDivisionId.substringAfter(countryDelimiter,"")
                .substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter,"")
                .substringBefore("/")
        val any=ocdDivisionId.substringAfter(anyDelimiter,"")
            .substringBefore("/")
        return Division(ocdDivisionId, country, state,any)
    }

    @ToJson
    fun divisionToJson (division: Division): String {
        return division.id
    }
}