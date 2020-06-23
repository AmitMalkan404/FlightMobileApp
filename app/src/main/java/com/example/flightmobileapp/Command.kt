package com.example.flightmobileapp

import com.google.gson.annotations.SerializedName

data class Command(

    val aileron: Double,

    val elevator: Double,

    val rudder: Double,

    val throttle: Double
)