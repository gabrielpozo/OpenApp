package com.gabrielpozo.openapp.api.main

import androidx.lifecycle.LiveData
import com.gabrielpozo.openapp.api.GenericResponse
import com.gabrielpozo.openapp.api.main.responses.BlogListSearchResponse
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

    @PUT("account/change_password/")
    @FormUrlEncoded
    fun updatePassword(
        @Header("Authorization") authorization: String,
        @Field("old_password") currentPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_new_password") confirmNewPassword: String
    ): LiveData<GenericApiResponse<GenericResponse>>


    @GET("blog/list")
    fun searchListBlogPosts(
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("page") page: Int
    ): LiveData<GenericApiResponse<BlogListSearchResponse>>
}