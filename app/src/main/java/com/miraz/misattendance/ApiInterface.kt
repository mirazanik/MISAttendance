package com.miraz.misattendance


import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


/**
 * Created by Md Miraz Hossain on 01-Aug-23.
 * miraz.anik@gmail.com
 */

interface ApiInterface {

    @Multipart
    @POST("registration")
    fun registerUser(
        @Part("name") name: RequestBody,
        @Part("staff_id") staff_id: RequestBody,
        @Part("department") department: RequestBody,
        @Part("designation") designation: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<RequestBody>

    @GET("customers/app_customer_visited_logs")
    fun visitedLog(
    ): Call<VisitedLogRP>

}