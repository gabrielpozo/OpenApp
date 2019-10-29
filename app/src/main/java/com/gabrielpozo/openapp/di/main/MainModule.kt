package com.gabrielpozo.openapp.di.main

import com.gabrielpozo.openapp.api.main.OpenMainService
import com.gabrielpozo.openapp.persistence.AccountPropertiesDao
import com.gabrielpozo.openapp.persistence.AppDataBase
import com.gabrielpozo.openapp.persistence.BlogPostDao
import com.gabrielpozo.openapp.repository.main.AccountRepository
import com.gabrielpozo.openapp.repository.main.BlogRepository
import com.gabrielpozo.openapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideApiMainService(retrofitBuilder: Retrofit.Builder): OpenMainService {
        return retrofitBuilder.build().create(OpenMainService::class.java)
    }


    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDataBase): BlogPostDao {
        return db.getBlogPostDao()
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        openMainService: OpenMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {
        return AccountRepository(openMainService, accountPropertiesDao, sessionManager)
    }

    @MainScope
    @Provides
    fun provideBlogRepository(
        openMainService: OpenMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogRepository {
        return BlogRepository(openMainService, blogPostDao, sessionManager)
    }
}