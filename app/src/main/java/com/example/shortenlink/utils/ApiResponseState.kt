package com.example.shortenlink.utils

sealed class ApiResponseState<T>(
    val myData: T? = null,
    val message: String?= null
) {
    class SuccessState<T>(data: T): ApiResponseState<T>(data)
    class ErrorState<T>(data: T?= null, errorMessage: String): ApiResponseState<T>(data,errorMessage)
    class InProgressState<T>: ApiResponseState<T>()
}