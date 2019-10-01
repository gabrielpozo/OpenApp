package com.gabrielpozo.openapp.repository.auth

import com.gabrielpozo.openapp.api.auth.OpenApiAuthService
import com.gabrielpozo.openapp.persistence.AccountPropertiesDao
import com.gabrielpozo.openapp.persistence.AuthTokenDao
import com.gabrielpozo.openapp.session.SessionManager

class AuthRepository(authTokenDao: AuthTokenDao,
                     accountPropertiesDao: AccountPropertiesDao,
                     openApiAuthService: OpenApiAuthService,
                     sessionManager: SessionManager) {
}