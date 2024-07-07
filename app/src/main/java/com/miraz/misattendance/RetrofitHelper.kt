package com.miraz.misattendance

/**
 * Created by Md Miraz Hossain on 01-Aug-23.
 * miraz.anik@gmail.com
 */


import android.util.Log.VERBOSE
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {

    @JvmStatic
    fun getInstance(url: String): Retrofit {

        val client = OkHttpClient.Builder()
            .addInterceptor(
                LoggingInterceptor.Builder()
                    .setLevel(Level.BASIC)
                    .log(VERBOSE)
                   // .addHeader("Authorization", "Bearer $TOKEN")
                    .build()
            ).build()


        val retrofit: Retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit

    }
}
