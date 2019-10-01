package com.gabrielpozo.openapp.session

import android.app.Application
import com.gabrielpozo.openapp.persistence.AuthTokenDao

class SessionManager(val authTokenDao: AuthTokenDao, val application: Application) {
}