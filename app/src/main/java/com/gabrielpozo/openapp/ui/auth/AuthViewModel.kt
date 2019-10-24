package com.gabrielpozo.openapp.ui.auth

import androidx.lifecycle.LiveData
import com.gabrielpozo.openapp.models.AuthToken
import com.gabrielpozo.openapp.repository.auth.AuthRepository
import com.gabrielpozo.openapp.session.SessionManager
import com.gabrielpozo.openapp.ui.BaseViewModel
import com.gabrielpozo.openapp.ui.DataState
import com.gabrielpozo.openapp.ui.auth.state.AuthStateEvent
import com.gabrielpozo.openapp.ui.auth.state.AuthStateEvent.*
import com.gabrielpozo.openapp.ui.auth.state.AuthViewState
import com.gabrielpozo.openapp.ui.auth.state.LoginFields
import com.gabrielpozo.openapp.ui.auth.state.RegistrationFields
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val sessionManager: SessionManager
) :
    BaseViewModel<AuthStateEvent, AuthViewState>() {

    override fun initViewState(): AuthViewState {
        return AuthViewState()
    }

    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        return when (stateEvent) {
            is LoginAttemptStateEvent -> {
                authRepository.attemptLogin(stateEvent.email, stateEvent.password)
            }

            is RegisterAttemptStateEvent -> {
                authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirm_password
                )
            }

            is CheckPreviousAuthEvent -> {
                authRepository.checkPreviousAuthUser()
            }
        }
    }

    fun setRegistrationFields(registrationFields: RegistrationFields) {
        val update = getCurrentNewStateOrNew()
        if (update.registrationFields == registrationFields)
            return

        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields) {
        val update = getCurrentNewStateOrNew()
        if (update.loginFields == loginFields)
            return

        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentNewStateOrNew()
        if (update.authToken == authToken)
            return

        update.authToken = authToken
        _viewState.value = update
    }

    fun login(authToken: AuthToken) {
        sessionManager.login(authToken)
    }

    fun cancelActiveJobs() {
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}