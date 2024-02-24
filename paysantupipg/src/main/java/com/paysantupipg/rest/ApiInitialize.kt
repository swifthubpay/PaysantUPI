package com.rest

import com.google.gson.GsonBuilder
import com.paysantupipg.rest.ApiInterface
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiInitialize {
    //var MAIN_URL_API = "http://parthshah.website/solid/api/"  //Local
    var MAIN_URL_API = "https://payment.paysants.in/api/"  //Local

    private var retrofit: Retrofit? = null
    private lateinit var apiInIt: ApiInterface

    @Synchronized
    fun getApiInIt(): ApiInterface {
        return apiInIt
    }

    fun initialize(): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(MAIN_URL_API)
                .client(requestHeader)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        return retrofit!!
    }


    fun initialize(baseUrl: String): ApiInterface {

        val gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(requestHeader)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        apiInIt = retrofit!!.create(ApiInterface::class.java)
        return apiInIt
    }

    private val requestHeader: OkHttpClient
        get() {
            return OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
        }

}