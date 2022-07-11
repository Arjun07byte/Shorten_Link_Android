package com.example.shortenlink.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shortenlink.repository.ShortenLinkRepository

class ShortenLinkVMProvider(
    private val app: Application,
    private val newsRepository: ShortenLinkRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ShortenLinkViewModel(app, newsRepository) as T
    }
}