package com.miraz.misattendance

import android.util.Log.VERBOSE
import com.ihsanbal.logging.Level
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.ihsanbal.logging.LoggingInterceptor

object RetrofitClient {
    private const val BASE_URL = "http://192.168.101.230:6969/"


    val client = OkHttpClient.Builder()
        .addInterceptor(
            LoggingInterceptor.Builder()
                .setLevel(Level.BASIC)
                .log(VERBOSE)
                .build()
        ).build()


//    private val client = OkHttpClient.Builder().build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
