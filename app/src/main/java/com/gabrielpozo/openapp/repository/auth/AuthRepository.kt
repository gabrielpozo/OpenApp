package com.gabrielpozo.openapp.repository.auth

import com.gabrielpozo.openapp.api.auth.OpenApiAuthService
import com.gabrielpozo.openapp.persistence.AccountPropertiesDao
import com.gabrielpozo.openapp.persistence.AuthTokenDao
import com.gabrielpozo.openapp.session.SessionManager
import javax.inject.Inject


class AuthRepository @Inject constructor(val authTokenDao: AuthTokenDao,
                     val accountPropertiesDao: AccountPropertiesDao,
                     val openApiAuthService: OpenApiAuthService,
                     val sessionManager: SessionManager) {
}