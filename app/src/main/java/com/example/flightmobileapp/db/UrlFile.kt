package com.example.flightmobileapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Url_Names")
data class UrlFile (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Url_id")
    val id : Int,
    @ColumnInfo(name = "Url_name")
    val name: String,
    @ColumnInfo(name = "connection_date")
    val date: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "succses")
    val isConnect: Boolean
)