package com.example.flightmobileapp.db

import com.example.flightmobileapp.db.UrlDAO
import com.example.flightmobileapp.db.UrlFile

class UrlRepository(private val dao: UrlDAO) {
    //val urls = dao.getAllUrls()
    val urls = dao.getAllLast5Urls()
    suspend fun insert(urlFile: UrlFile){
        dao.insertUrl(urlFile)
    }
    suspend fun update(urlFile: UrlFile){
        dao.updateUrl(urlFile)
    }
    suspend fun delete(urlFile: UrlFile){
        dao.deleteUrl(urlFile)
    }
    suspend fun deleteAll() : Int {
        return dao.deleteAll()
    }
}