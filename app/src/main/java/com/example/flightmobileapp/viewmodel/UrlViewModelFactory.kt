package com.example.flightmobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flightmobileapp.db.UrlRepository
import java.lang.IllegalArgumentException

class UrlViewModelFactory(private val repository: UrlRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       if(modelClass.isAssignableFrom(UrlViewModel::class.java)){
           return UrlViewModel(repository) as T
       }
        throw IllegalArgumentException("Unknown view model class")
    }
}