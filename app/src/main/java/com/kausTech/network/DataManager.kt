package com.kausTech.network

import com.kausTech.babynames.ui.fragments.Names
import com.kausTech.network.model.BaseResponse
import com.kausTech.network.model.Regions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager @Inject constructor(private var apiInterface: ApiServices) {
    suspend fun getNames(): BaseResponse<List<Names>> {
        return apiInterface.getBabyNames()
    }

    suspend fun getRegion(): List<Regions> {
        return apiInterface.getRegions()
    }
}