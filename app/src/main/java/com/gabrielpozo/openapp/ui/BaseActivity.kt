package com.gabrielpozo.openapp.ui

import com.gabrielpozo.openapp.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {
    private val TAG: String = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

}