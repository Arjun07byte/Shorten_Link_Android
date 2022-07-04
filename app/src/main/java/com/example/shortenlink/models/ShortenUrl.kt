package com.example.shortenlink.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "ShortenUrlTable"
)
data class ShortenUrl(
    @PrimaryKey
    val title: String,
    val date: String,
    val fullLink: String,
    val shortLink: String,
    val status: Int
)