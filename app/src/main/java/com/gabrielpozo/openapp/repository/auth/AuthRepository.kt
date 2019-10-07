package com.gabrielpozo.openapp.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import com.gabrielpozo.openapp.api.auth.OpenApiAuthService
import com.gabrielpozo.openapp.api.auth.network_responses.LoginResponse
import com.gabrielpozo.openapp.api.auth.network_responses.RegistrationResponse
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
import com.gabrielpozo.openapp.util.ApiSuccessResponse
import com.gabrielpozo.openapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.gabrielpozo.openapp.util.GenericApiResponse
import kotlinx.coroutines.Job
import javax.inject.Inject


class AuthRepository @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {

    private val TAG: String = "Gabriel"
    private var authRepoJob: Job? = null


    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginFieldErrors = LoginFields(email, password).isValidForLogin()
        if (loginFieldErrors != LoginFields.LoginError.none()) {
            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog)
        }
        return object : NetworkBoundResource<LoginResponse, AuthViewState>(sessionManager.isConnectedToTheInternet()) {
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

                onCompleteJob(
                    DataState.dataState(
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

        }.asLiveData()
    }


    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationFieldsErrors = RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()
        if (registrationFieldsErrors != RegistrationFields.RegistrationError.none()) {
            returnErrorResponse(registrationFieldsErrors, ResponseType.Dialog)
        }

        return object : NetworkBoundResource<RegistrationResponse, AuthViewState>(sessionManager.isConnectedToTheInternet()) {
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

                onCompleteJob(
                    DataState.dataState(
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

        }.asLiveData()

    }

    private fun returnErrorResponse(
        errorMessage: String,
        responseType: ResponseType.Dialog
    ): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                value = DataState.error(Response(errorMessage, responseType))
            }
        }
    }

    fun cancelActiveJobs() {
        Log.d(TAG, "Cancelling on-going jobs..")
        authRepoJob?.cancel()
    }

}

