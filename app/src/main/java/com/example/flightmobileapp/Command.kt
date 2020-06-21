package com.example.flightmobileapp

import com.google.gson.annotations.SerializedName

data class Command(
    @SerializedName("aileron")
    val aileron: Double,
    @SerializedName("elevator")
    val elevator: Double,
    @SerializedName("rudder")
    val rudder: Double,
    @SerializedName("throttle")
    val throttle: Double
)