package com.gabrielpozo.openapp.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.gabrielpozo.openapp.api.auth.OpenApiAuthService
import com.gabrielpozo.openapp.models.AuthToken
import com.gabrielpozo.openapp.persistence.AccountPropertiesDao
import com.gabrielpozo.openapp.persistence.AuthTokenDao
import com.gabrielpozo.openapp.session.SessionManager
import com.gabrielpozo.openapp.ui.DataState
import com.gabrielpozo.openapp.ui.Response
import com.gabrielpozo.openapp.ui.ResponseType
import com.gabrielpozo.openapp.ui.auth.state.AuthViewState
import com.gabrielpozo.openapp.util.ApiEmptyResponse
import com.gabrielpozo.openapp.util.ApiErrorResponse
import com.gabrielpozo.openapp.util.ApiSuccessResponse
import com.gabrielpozo.openapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import javax.inject.Inject


class AuthRepository @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.login(email, password).switchMap { response ->
            object : LiveData<DataState<AuthViewState>>() {
                override fun onActive() {
                    when (response) {
                        is ApiSuccessResponse -> {
                            value = DataState.dataState(
                                AuthViewState(
                                    authToken = AuthToken(
                                        response.body.pk,
                                        response.body.token
                                    )
                                ), response = null
                            )
                        }

                        is ApiErrorResponse -> {
                            value = DataState.error(
                                response = Response(
                                    message = response.errorMessage,
                                    responseType = ResponseType.Dialog
                                )
                            )
                        }

                        is ApiEmptyResponse -> {
                            value = DataState.error(
                                response = Response(
                                    message = ERROR_UNKNOWN,
                                    responseType = ResponseType.Dialog
                                )
                            )
                        }
                    }
                }
            }
        }

    }

    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.register(email, username, password, confirmPassword)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        when (response) {
                            is ApiSuccessResponse -> {
                                value = DataState.dataState(
                                    AuthViewState(
                                        authToken = AuthToken(
                                            response.body.pk,
                                            response.body.token
                                        )
                                    ), response = null
                                )
                            }

                            is ApiErrorResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = response.errorMessage,
                                        responseType = ResponseType.Dialog
                                    )
                                )
                            }

                            is ApiEmptyResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog
                                    )
                                )
                            }

                        }
                    }
                }
            }

    }

}

