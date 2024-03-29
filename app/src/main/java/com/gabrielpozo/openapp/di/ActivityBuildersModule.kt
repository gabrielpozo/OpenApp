package com.gabrielpozo.openapp.di

import com.gabrielpozo.openapp.di.auth.AuthFragmentBuildersModule
import com.gabrielpozo.openapp.di.auth.AuthModule
import com.gabrielpozo.openapp.di.auth.AuthScope
import com.gabrielpozo.openapp.di.auth.AuthViewModelModule
import com.gabrielpozo.openapp.di.main.MainFragmentBuildersModule
import com.gabrielpozo.openapp.di.main.MainModule
import com.gabrielpozo.openapp.di.main.MainScope
import com.gabrielpozo.openapp.di.main.MainViewModelModule
import com.gabrielpozo.openapp.ui.auth.AuthActivity
import com.gabrielpozo.openapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity
}