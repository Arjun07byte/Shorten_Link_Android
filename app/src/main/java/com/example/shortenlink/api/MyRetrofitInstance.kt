package com.example.shortenlink.api

import com.example.shortenlink.utils.Constants
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyRetrofitInstance {
    companion object{
        private val retrofitInst by lazy {
            val loggingVar = HttpLoggingInterceptor()
            loggingVar.setLevel(HttpLoggingInterceptor.Level.BODY)
            val clientVar = okhttp3.OkHttpClient.Builder()
                .addInterceptor(loggingVar)
                .build()

            Retrofit.Builder()
                .baseUrl(Constants.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientVar)
                .build()
        }

        val apiVariable: CuttlyAPI by lazy {
            retrofitInst.create(CuttlyAPI::class.java)
        }
    }
}