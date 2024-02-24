package com.paysantupipg.rest

import com.rest.ApiResponseManager

interface ApiResponseInterface {

    fun getApiResponse(apiResponseManager: ApiResponseManager<*>)

    fun onFailure(apiResponseManager: ApiResponseManager<*>, error_message: String, error: Boolean)

}