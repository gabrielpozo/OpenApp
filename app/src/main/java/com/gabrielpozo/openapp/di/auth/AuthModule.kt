package com.gabrielpozo.openapp.di.auth

import com.gabrielpozo.openapp.api.auth.OpenApiAuthService
import com.gabrielpozo.openapp.persistence.AccountPropertiesDao
import com.gabrielpozo.openapp.persistence.AuthTokenDao
import com.gabrielpozo.openapp.repository.auth.AuthRepository
import com.gabrielpozo.openapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule {


    @AuthScope
    @Provides
    fun provideFakeApiService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager
        )
    }

}