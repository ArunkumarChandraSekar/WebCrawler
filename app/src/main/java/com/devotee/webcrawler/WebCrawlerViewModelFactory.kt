package com.devotee.webcrawler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WebCrawlerViewModelFactory():ViewModelProvider.Factory
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(WebCrawlerViewModel::class.java)){
            return WebCrawlerViewModel() as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }
}