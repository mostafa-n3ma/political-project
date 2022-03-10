package com.example.android.politicalpreparedness.data.source.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    // Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: Election)

    // Add select all election query
    @Query("SELECT * FROM election_table ORDER BY id DESC")
    suspend fun getAllElections():List<Election>

    // Add select single election query
    @Query("SELECT * FROM election_table WHERE id=:id")
    suspend fun getElection(id:Int):Election?

    // Add delete query
    @Query("DELETE FROM election_table WHERE id=:id")
    suspend fun deleteElection(id:Int)

    // Add clear query
    @Query("DELETE FROM election_table")
    suspend fun clear()

}