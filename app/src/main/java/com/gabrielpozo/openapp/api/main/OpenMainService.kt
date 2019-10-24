package com.gabrielpozo.openapp.api.main

import androidx.lifecycle.LiveData
import com.gabrielpozo.openapp.api.GenericResponse
import com.gabrielpozo.openapp.models.AccountProperties
import com.gabrielpozo.openapp.models.AuthToken
import com.gabrielpozo.openapp.util.GenericApiResponse
import retrofit2.http.*

interface OpenMainService {

    @GET("account/properties")
    fun getAccountProperties(@Header("Authorization") authorization: String): LiveData<GenericApiResponse<AccountProperties>>


    @PUT("account/properties/update")
    @FormUrlEncoded
    fun saveAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("username") username: String
    ): LiveData<GenericApiResponse<GenericResponse>>

}