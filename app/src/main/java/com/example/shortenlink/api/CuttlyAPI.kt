package com.example.shortenlink.api

import com.example.shortenlink.models.ApiCallResult
import com.example.shortenlink.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CuttlyAPI {
    @GET("api/api.php")
    suspend fun getMyLinkShortened(
        @Query("key")
        myKey : String = Constants.myAPIKey,
        @Query("short")
        myLongerUrl: String
    ): Response<ApiCallResult>
}