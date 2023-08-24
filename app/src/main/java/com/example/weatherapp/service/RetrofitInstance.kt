package com.example.weatherapp.service

import com.example.weatherapp.API
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {

    companion object {
        //retrofit instance
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder().addInterceptor(logging).build()

            Retrofit.Builder().baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        //service interface
        val api: Service by lazy {
            retrofit.create(Service::class.java)
        }
    }
}