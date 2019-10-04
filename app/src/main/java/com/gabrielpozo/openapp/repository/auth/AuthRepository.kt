package com.gabrielpozo.openapp.repository.auth

import androidx.lifecycle.LiveData
import com.gabrielpozo.openapp.api.auth.OpenApiAuthService
import com.gabrielpozo.openapp.api.auth.network_responses.LoginResponse
import com.gabrielpozo.openapp.api.auth.network_responses.RegistrationResponse
import com.gabrielpozo.openapp.persistence.AccountPropertiesDao
import com.gabrielpozo.openapp.persistence.AuthTokenDao
import com.gabrielpozo.openapp.session.SessionManager
import com.gabrielpozo.openapp.util.GenericApiResponse
import javax.inject.Inject


class AuthRepository @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {

    fun testLoginRequest(
        email: String,
        password: String
    ): LiveData<GenericApiResponse<LoginResponse>> {
        return openApiAuthService.login(email, password)

    }

    fun testRegistrationRequest(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<GenericApiResponse<RegistrationResponse>> {
        return openApiAuthService.register(email, username, password, confirmPassword)

    }
}