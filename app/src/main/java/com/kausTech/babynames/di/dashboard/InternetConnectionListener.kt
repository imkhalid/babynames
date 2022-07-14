package com.kausTech.babynames.di.dashboard

internal interface InternetConnectionListener {
    fun onInternetUnavailable()
    fun onCacheUnavailable()
}