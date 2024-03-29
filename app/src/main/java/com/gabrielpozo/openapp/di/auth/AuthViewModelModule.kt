package com.gabrielpozo.openapp.di.auth

import androidx.lifecycle.ViewModel
import com.gabrielpozo.openapp.di.ViewModelKey
import com.gabrielpozo.openapp.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel
}