package com.gabrielpozo.openapp.di.main

import androidx.lifecycle.ViewModel
import com.gabrielpozo.openapp.di.ViewModelKey
import com.gabrielpozo.openapp.ui.main.account.AccountViewModel
import com.gabrielpozo.openapp.ui.main.blog.viewmodel.BlogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAuthViewModel(accountViewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel


}