package com.gabrielpozo.openapp.repository.main

import android.util.Log
import com.gabrielpozo.openapp.api.main.OpenMainService
import com.gabrielpozo.openapp.persistence.AccountPropertiesDao
import com.gabrielpozo.openapp.session.SessionManager
import kotlinx.coroutines.Job
import javax.inject.Inject

class AccountRepository @Inject constructor(
    val openMainService: OpenMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) {
    private val TAG: String = "Gabriel"

    private val repositoryJob: Job? = null

    fun cancelActiveJobs(){
        Log.d(TAG,"AuthRepository: cancelling on-going jobs...")

    }
}