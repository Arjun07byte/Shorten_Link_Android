package com.example.shortenlink.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shortenlink.adapters.HistoryRvAdapter
import com.example.shortenlink.models.ApiCallResult
import com.example.shortenlink.models.ShortenUrl
import com.example.shortenlink.repository.ShortenLinkRepository
import com.example.shortenlink.utils.ApiResponseState
import com.example.shortenlink.utils.HomeApplication
import kotlinx.coroutines.launch
import retrofit2.Response

class ShortenLinkViewModel(
    myApplication: Application,
    private val myRepository: ShortenLinkRepository
): AndroidViewModel(myApplication) {
    val apiResultList: MutableLiveData<ApiResponseState<ShortenUrl>> = MutableLiveData()

    private fun handleApiResponses(responseCollected: Response<ApiCallResult>): ApiResponseState<ShortenUrl> {
        if(responseCollected.isSuccessful){
            responseCollected.body()?.let { apiCallResult ->
                when(apiCallResult.url.status){
                    7 -> return ApiResponseState.SuccessState(apiCallResult.url)
                    1 -> return ApiResponseState.ErrorState(errorMessage = "Link Already Shortened")
                    2 , 5 -> return ApiResponseState.ErrorState(errorMessage = "Invalid Link")
                    else -> return ApiResponseState.ErrorState(errorMessage = "Error in Shortening")
                }
            }
        }

        return ApiResponseState.ErrorState(errorMessage = "Error in Shorting")
    }

    fun getMyLinkShortened(longUrl: String) = viewModelScope.launch {
        apiResultList.postValue(ApiResponseState.InProgressState())
        if(isInternetConnected()){
            val responseCollected = myRepository.getMyLinkShortened(longUrl)
            apiResultList.postValue(handleApiResponses(responseCollected))
        } else {
            apiResultList.postValue(ApiResponseState.ErrorState(errorMessage = "No Internet Connection"))
        }
    }

    fun insertShortLink(givenLink: ShortenUrl) = viewModelScope.launch {
        myRepository.insert(givenLink)
    }

    fun getAllLinks() = myRepository.getAllShortenedLink()

    private fun isInternetConnected() : Boolean{
        val connectivityManager = getApplication<HomeApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        return connectivityManager.run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
        } ?: false
    }
}