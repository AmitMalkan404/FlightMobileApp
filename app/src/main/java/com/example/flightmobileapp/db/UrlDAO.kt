package com.example.flightmobileapp.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UrlDAO {
    @Insert
    suspend fun insertUrl(urlFile: UrlFile) : Long
    @Update
    suspend fun updateUrl(urlFile: UrlFile)

    @Delete
    suspend fun deleteUrl(urlFile: UrlFile)

    @Query("DELETE FROM Url_Names")
    suspend fun deleteAll() : Int

    @Query("SELECT * FROM Url_Names")
    fun getAllUrls() : LiveData<List<UrlFile>>

    @Query("SELECT * FROM Url_Names ORDER BY connection_date DESC LIMIT 5")
    fun getAllLast5Urls() : LiveData<List<UrlFile>>



//
//    @Insert
//    fun insertUrl2(urlFile: UrlFile) : Long
//
//    @Insert
//    fun insertUrls(urlFile1: UrlFile,urlFile2: UrlFile,urlFile3: UrlFile) : List<Long>
//
//    @Insert
//    fun insertUrls(urlFile: List<UrlFile>) : List<Long>

}