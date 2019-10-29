package com.gabrielpozo.openapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.gabrielpozo.openapp.api.GenericResponse
import com.gabrielpozo.openapp.api.main.OpenMainService
import com.gabrielpozo.openapp.models.AccountProperties
import com.gabrielpozo.openapp.models.AuthToken
import com.gabrielpozo.openapp.persistence.AccountPropertiesDao
import com.gabrielpozo.openapp.repository.JobManager
import com.gabrielpozo.openapp.repository.NetworkBoundResource
import com.gabrielpozo.openapp.session.SessionManager
import com.gabrielpozo.openapp.ui.DataState
import com.gabrielpozo.openapp.ui.Response
import com.gabrielpozo.openapp.ui.ResponseType
import com.gabrielpozo.openapp.ui.main.account.state.AccountViewState
import com.gabrielpozo.openapp.util.AbsentLiveData
import com.gabrielpozo.openapp.util.ApiSuccessResponse
import com.gabrielpozo.openapp.util.Constants
import com.gabrielpozo.openapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository @Inject constructor(
    val openMainService: OpenMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) : JobManager("AccountRepository") {
    private val TAG: String = "Gabriel"

    fun getAccountProperties(
        authToken: AuthToken
    ): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openMainService.getAccountProperties("Token ${authToken.token}")
            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    //finish by viewing the db cache
                    val dbSource = loadFromCache()
                    result.addSource(dbSource) { accountViewState ->
                        onCompleteJob(DataState.data(accountViewState))
                        result.removeSource(dbSource)
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("getAccountProperties", job)
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap { accountProperties ->
                        object : LiveData<AccountViewState>() {
                            override fun onActive() {
                                super.onActive()
                                accountProperties.timestamp =
                                    (System.currentTimeMillis() / 1000).toInt()
                                value = AccountViewState(accountProperties)
                            }
                        }
                    }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountProperties>) {
                updateLocalDatabase(response.body)
                createCacheRequestAndReturn()
            }


            override suspend fun updateLocalDatabase(cacheObject: AccountProperties?) {
                cacheObject?.let {
                    accountPropertiesDao.updateAccountProperties(
                        cacheObject.pk,
                        cacheObject.email,
                        cacheObject.userName,
                        getCurrentTimestamp()
                    )
                }
            }

            override fun shouldFetch(): Boolean {
                val properties = accountPropertiesDao.searchTimeStampByPk(authToken.account_pk!!)
                return properties.timestamp?.let { timestamp ->
                    (((getCurrentTimestamp() - timestamp) >= Constants.accountproperties_refresh_time))
                } ?: true
            }

            fun getCurrentTimestamp() = (System.currentTimeMillis() / 1000).toInt()


        }.asLiveData()


    }

    fun saveAccountProperties(
        authToken: AuthToken,
        accountProperties: AccountProperties
    ): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            isNetworkAvailable = sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldLoadFromCache = false,
            shouldCancelIfnotInternet = true

        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                updateLocalDatabase(null)
                withContext(Main) {
                    //finish with success response
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(response.body.response, ResponseType.Toast)
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openMainService.saveAccountProperties(
                    "Token ${authToken.token!!}",
                    accountProperties.email,
                    accountProperties.userName
                )
            }

            //Not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            //not used in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDatabase(cacheObject: Any?) {
                return accountPropertiesDao.updateAccountProperties(
                    accountProperties.pk,
                    accountProperties.email,
                    accountProperties.userName,
                    Constants.accountproperties_update_immediately
                )
            }

            override fun setJob(job: Job) {
                addJob("saveAccountProperties", job)
            }

        }.asLiveData()
    }


    fun updatePassword(
        authToken: AuthToken,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true, true, false
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Main) {
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(response.body.response, ResponseType.Toast)
                        )
                    )

                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openMainService.updatePassword(
                    "Token ${authToken.token}",
                    currentPassword,
                    newPassword,
                    confirmNewPassword
                )
            }

            //Not applicable
            override suspend fun createCacheRequestAndReturn() {
            }

            //not applicable in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDatabase(cacheObject: Any?) {
            }

            override fun setJob(job: Job) {
                addJob("updatePassword", job)
            }

        }.asLiveData()
    }
}