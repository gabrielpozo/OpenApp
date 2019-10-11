package com.gabrielpozo.openapp.di.main

import com.gabrielpozo.openapp.ui.main.account.AccountFragment
import com.gabrielpozo.openapp.ui.main.account.ChangePasswordFragment
import com.gabrielpozo.openapp.ui.main.account.UpdateAccountFragment
import com.gabrielpozo.openapp.ui.main.blog.BlogFragment
import com.gabrielpozo.openapp.ui.main.blog.UpdateBlogFragment
import com.gabrielpozo.openapp.ui.main.blog.ViewBlogFragment
import com.gabrielpozo.openapp.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {
    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}