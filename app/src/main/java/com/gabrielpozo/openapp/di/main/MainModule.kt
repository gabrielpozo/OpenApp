package com.gabrielpozo.openapp.di.main

import com.gabrielpozo.openapp.api.main.OpenMainService
import com.gabrielpozo.openapp.persistence.AccountPropertiesDao
import com.gabrielpozo.openapp.repository.main.AccountRepository
import com.gabrielpozo.openapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideApiMainService(retrofitBuilder: Retrofit.Builder): OpenMainService {
        return retrofitBuilder.build().create(OpenMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideMainAccountRepository(
        openMainService: OpenMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {
        return AccountRepository(openMainService, accountPropertiesDao, sessionManager)
    }
}