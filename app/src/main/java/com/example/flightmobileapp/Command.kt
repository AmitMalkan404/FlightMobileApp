package com.example.flightmobileapp

import com.google.gson.annotations.SerializedName

data class Command(
    val aileron: Float,
    val elevator: Float,
    val rudder: Float,
    val throttle: Float
)