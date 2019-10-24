package com.gabrielpozo.openapp.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.gabrielpozo.openapp.api.auth.OpenApiAuthService
import com.gabrielpozo.openapp.api.auth.network_responses.LoginResponse
import com.gabrielpozo.openapp.api.auth.network_responses.RegistrationResponse
import com.gabrielpozo.openapp.models.AccountProperties
import com.gabrielpozo.openapp.models.AuthToken
import com.gabrielpozo.openapp.persistence.AccountPropertiesDao
import com.gabrielpozo.openapp.persistence.AuthTokenDao
import com.gabrielpozo.openapp.repository.NetworkBoundResource
import com.gabrielpozo.openapp.session.SessionManager
import com.gabrielpozo.openapp.ui.DataState
import com.gabrielpozo.openapp.ui.Response
import com.gabrielpozo.openapp.ui.ResponseType
import com.gabrielpozo.openapp.ui.auth.state.AuthViewState
import com.gabrielpozo.openapp.ui.auth.state.LoginFields
import com.gabrielpozo.openapp.ui.auth.state.RegistrationFields
import com.gabrielpozo.openapp.util.*
import com.gabrielpozo.openapp.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.gabrielpozo.openapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.gabrielpozo.openapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject


class AuthRepository @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharePrefrences: SharedPreferences,
    val sharePrefrencesEditor: SharedPreferences.Editor
) {

    private val TAG: String = "Gabriel"
    private var authRepoJob: Job? = null


    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginFieldErrors = LoginFields(email, password).isValidForLogin()
        if (loginFieldErrors != LoginFields.LoginError.none()) {
            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog)
        }
        return object :
            NetworkBoundResource<LoginResponse, Any, AuthViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                false,
                true
            ) {

            //not used in this case - not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")
                //Incorrect login credentials counts as a 200 code response from server, so need to handle that
                if (response.body.response == GENERIC_AUTH_ERROR) {
                    return onErrorReturn(
                        response.body.errorMessage,
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                }

                //don't care about the result. Just insert if it doesn't exist b/c foreign key relationship
                //with the authToken table
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        "",
                        Constants.accountproperties_update_immediately
                    )
                )
                //will return -1 if it failure
                val result =
                    authTokenDao.insertToken(AuthToken(response.body.pk, response.body.token))
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(
                                ERROR_SAVE_AUTH_TOKEN,
                                ResponseType.Dialog
                            )
                        )
                    )
                }

                saveUserToAuthenticatedSharedPrefs(email)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                cancelActiveJobs()
                authRepoJob = job
            }

            //not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            //not used in this case
            override suspend fun updateLocalDatabase(cacheObject: Any?) {
            }

        }.asLiveData()
    }


    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationFieldsErrors =
            RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()
        if (registrationFieldsErrors != RegistrationFields.RegistrationError.none()) {
            return returnErrorResponse(registrationFieldsErrors, ResponseType.Dialog)
        }

        return object :
            NetworkBoundResource<RegistrationResponse, Any, AuthViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                false,
                true

            ) {


            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")
                //Incorrect login credentials counts as a 200 code response from server, so need to handle that
                if (response.body.response == GENERIC_AUTH_ERROR) {
                    return onErrorReturn(
                        response.body.errorMessage,
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                }

                //don't care about the result. Just insert if it doesn't exist b/c foreign key relationship
                //with the authToken table
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        "",
                        Constants.accountproperties_update_immediately
                    )
                )
                //will return -1 if it failures
                val result =
                    authTokenDao.insertToken(AuthToken(response.body.pk, response.body.token))
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(
                                ERROR_SAVE_AUTH_TOKEN,
                                ResponseType.Dialog
                            )
                        )
                    )
                }
                saveUserToAuthenticatedSharedPrefs(email)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
                cancelActiveJobs()
                authRepoJob = job
            }

            //not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            //not used in this case
            override suspend fun updateLocalDatabase(cacheObject: Any?) {
            }

        }.asLiveData()
    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {
        val previousAuthUserEmail =
            sharePrefrences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
        if (previousAuthUserEmail.isNullOrBlank()) {
            Log.d("Gabriel", "checkPreviousAuthUser: NO Previously authenticated user found")
            return returnNotTokenFound()
        }
        return object : NetworkBoundResource<Void, AccountProperties, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            false,
            false
        ) {
            //not used in this case
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
            }

            //not used in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override suspend fun createCacheRequestAndReturn() {
                accountPropertiesDao.searchByEmail(previousAuthUserEmail)
                    ?.let { accountProperties ->
                        Log.d(TAG, "checkPreviousUser: searching for token: $previousAuthUserEmail")
                        if (accountProperties.pk > -1) {
                            authTokenDao.searchByPk(accountProperties.pk).let { authToken ->
                                if (authToken != null) {
                                    updateLocalDatabase(accountProperties)
                                    if (authToken.token != null) {
                                        onCompleteJob(
                                            DataState.data(
                                                data = AuthViewState(
                                                    authToken = authToken
                                                )
                                            )
                                        )
                                        return
                                    }
                                }
                            }
                        }

                    }
                onCompleteJob(
                    DataState.data(
                        data = null, response = Response(
                            RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None
                        )
                    )
                )
            }

            override fun setJob(job: Job) {
                cancelActiveJobs()
                authRepoJob = job
            }

            //not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            //not used in this case
            override suspend fun updateLocalDatabase(cacheObject: AccountProperties?) {
                cacheObject?.let {
                    accountPropertiesDao.updateAccountTimestampProperty(
                        cacheObject.pk,
                        Constants.accountproperties_update_immediately
                    )
                }
            }

        }.asLiveData()

    }

    private fun returnNotTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None)
                )
            }
        }

    }

    private fun saveUserToAuthenticatedSharedPrefs(email: String) {
        sharePrefrencesEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharePrefrencesEditor.apply()

    }

    private fun returnErrorResponse(
        errorMessage: String,
        responseType: ResponseType.Dialog
    ): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                Log.d(TAG, "ErrorResponse GAB $errorMessage")
                value = DataState.error(Response(errorMessage, responseType))
            }
        }
    }

    fun cancelActiveJobs() {
        Log.d(TAG, "Cancelling on-going jobs..")
        authRepoJob?.cancel()
    }

}

