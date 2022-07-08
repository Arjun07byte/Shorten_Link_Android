package com.example.shortenlink.repository

import com.example.shortenlink.api.MyRetrofitInstance
import com.example.shortenlink.localDatabase.ShortenLinkDatabase
import com.example.shortenlink.models.ShortenUrl

class ShortenLinkRepository(
    private val myDB: ShortenLinkDatabase
){
    suspend fun getMyLinkShortened(givenUrl: String) = MyRetrofitInstance.apiVariable.getMyLinkShortened(myLongerUrl = givenUrl)
    suspend fun insert(givenShortenedLink: ShortenUrl) = myDB.getShortenLinkDAO().insert(givenShortenedLink)
    fun getAllShortenedLink() = myDB.getShortenLinkDAO().getAllLinks()
}