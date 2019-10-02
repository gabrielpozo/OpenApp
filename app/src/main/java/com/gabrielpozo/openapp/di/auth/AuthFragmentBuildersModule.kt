package com.gabrielpozo.openapp.di.auth

import com.gabrielpozo.openapp.ui.auth.ForgotPasswordFragment
import com.gabrielpozo.openapp.ui.auth.LauncherFragment
import com.gabrielpozo.openapp.ui.auth.LoginFragment
import com.gabrielpozo.openapp.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {
    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

}