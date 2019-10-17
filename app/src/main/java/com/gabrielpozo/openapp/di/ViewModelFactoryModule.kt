package com.gabrielpozo.openapp.di

import androidx.lifecycle.ViewModelProvider
import com.gabrielpozo.openapp.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}