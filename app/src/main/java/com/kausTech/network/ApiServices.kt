package com.kausTech.network

import com.kausTech.babynames.ui.fragments.Names
import com.kausTech.network.model.BaseResponse
import com.kausTech.network.model.Regions
import retrofit2.http.GET

interface ApiServices {
    @GET("getbabynames")
    suspend fun getBabyNames():BaseResponse<List<Names>>

    @GET("origin")
    suspend fun getRegions():List<Regions>
}