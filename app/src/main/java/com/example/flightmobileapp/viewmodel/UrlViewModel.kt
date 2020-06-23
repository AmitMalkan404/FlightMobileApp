package com.example.flightmobileapp.viewmodel

import android.webkit.URLUtil
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightmobileapp.Api
import com.example.flightmobileapp.Event
import com.example.flightmobileapp.db.UrlFile
import com.example.flightmobileapp.db.UrlRepository
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UrlViewModel(private val repository: UrlRepository) : ViewModel(),Observable {
    val urls = repository.urls

    var isConnected = MutableLiveData<Boolean>()
    @Bindable
    val typeURL = MutableLiveData<String>()
    var typeURLtemp = ""
    @Bindable
    val urlTime = MutableLiveData<String>()
    @Bindable
    val connectButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage

    init{
       connectButtonText.value = "Connect"
    }


    fun getScreenShot(){
        val name = typeURL.value!!
        val gson = GsonBuilder()
            .setLenient()
            .create()
//            val retrofit = Retrofit.Builder()
//                .baseUrl("http://10.0.2.2:37819/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(name)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
                    statusMessage.value = Event("Connected!")
                    isConnected.value = true

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //connectFlag = false
                //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
                statusMessage.value = Event("No")
            }
        })
    }
    fun checkIfExist(myUrl : String) : Boolean {
        for (url in urls.value!!) {
            if (myUrl.equals(url.name)) {
                delete(url)
            }
        }
        return false
    }

    fun connect(){
        if(typeURL.value == null) {
            statusMessage.value = Event("Please enter an URL")
        }else if(!URLUtil.isValidUrl(typeURL.value)){
            statusMessage.value = Event("Please enter a correct URL")
        } else{
            val name = typeURL.value!!
            typeURLtemp = name
            var connectFlag : Boolean = true
            if(!checkIfExist(typeURLtemp)){
                insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
            }
            //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
            val gson = GsonBuilder().setLenient().create()
//            val retrofit = Retrofit.Builder()
//                .baseUrl("http://10.0.2.2:37819/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build()
            val retrofit = Retrofit.Builder().baseUrl(name).addConverterFactory(GsonConverterFactory
                .create(gson)).build()
            val api = retrofit.create(Api::class.java)
            //println(name)
            val body = api.getImg().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if(response.code() !in 400..598){
                        isConnected.value = true
                        //statusMessage.value = Event("Connected!")
                    } else{
                        statusMessage.value = Event("Didnt managed to connect!!")
                    }
//                    statusMessage.value = Event("Connected!")
//                    isConnected.value = true
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //connectFlag = false
                    //insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
                    statusMessage.value = Event("Could Not Connect. Try Again")
                }
            })
            typeURL.value = null
//
//
//            if(tryToConnect(name)){
//                insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
//
//            }else{
//                connectFlag = false
//                insert(UrlFile(id = 0, name = name, isConnect = connectFlag))
//
//            }
//            typeURL.value = null
        }
    }
    fun initUrlTextClicked(urlFile: UrlFile){
        typeURL.value = urlFile.name
    }

    fun clearAll():Job = viewModelScope.launch{
        val numOfUrlsDeleted = repository.deleteAll()
        if(numOfUrlsDeleted >0) {
            statusMessage.value = Event("All URL's Deleted Successfully")
        }else{
            statusMessage.value = Event("There are no URL's to delete")
        }
    }
    fun insert(urlFile: UrlFile) :Job = viewModelScope.launch {
            repository.insert(urlFile)
        statusMessage.value = Event("Url Added Successfully")
        }
    fun delete(urlFile: UrlFile) :Job = viewModelScope.launch {
        repository.delete(urlFile)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}