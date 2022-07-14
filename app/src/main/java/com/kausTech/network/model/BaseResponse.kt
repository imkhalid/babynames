package com.kausTech.network.model

data class BaseResponse<T>(
    val count:String,
    val num_page:String,
    val results:T
)