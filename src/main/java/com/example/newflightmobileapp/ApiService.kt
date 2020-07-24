package com.example.newflightmobileapp

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    // Setting the GET request to get the screenshot form the http server
    @GET("/screenshot")
    fun getScreenshot(): Call<ResponseBody>

    // Setting the POST request to send the command to the http server
    @Headers("Content-Type: application/json")
    @POST("/api/Command")
    fun addCommand(@Body userData: Command): Call<ResponseBody>

}
