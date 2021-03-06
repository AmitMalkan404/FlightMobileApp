package com.example.flightmobileapp

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {
    @GET("/screenshot")
    fun getImg(): Call<ResponseBody>

    @POST("api/command")
    fun sendCommand(@Body command: Command): Call<ResponseBody>
//    @POST("api/command")
//    fun sendCommand(@Body requestBody: RequestBody): Call<ResponseBody>
}
