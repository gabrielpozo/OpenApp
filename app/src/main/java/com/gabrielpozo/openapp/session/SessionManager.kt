package com.gabrielpozo.openapp.session

import android.app.Application
import com.gabrielpozo.openapp.persistence.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
}