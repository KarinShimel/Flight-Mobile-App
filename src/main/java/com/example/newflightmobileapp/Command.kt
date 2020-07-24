package com.example.newflightmobileapp

import com.google.gson.annotations.SerializedName

/*
* Command class to represent the command being sent to the server.
* */
data class Command(
    @SerializedName("aileron") val Aileron: Double,
    @SerializedName("rudder") val Rudder: Double,
    @SerializedName("elevator") val Elevator: Double,
    @SerializedName("throttle") val Throttle: Double
)