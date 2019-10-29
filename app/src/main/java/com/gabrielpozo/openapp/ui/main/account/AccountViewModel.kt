package com.gabrielpozo.openapp.ui.main.account

import androidx.lifecycle.LiveData
import com.gabrielpozo.openapp.models.AccountProperties
import com.gabrielpozo.openapp.repository.main.AccountRepository
import com.gabrielpozo.openapp.session.SessionManager
import com.gabrielpozo.openapp.ui.BaseViewModel
import com.gabrielpozo.openapp.ui.DataState
import com.gabrielpozo.openapp.ui.auth.state.AuthStateEvent
import com.gabrielpozo.openapp.ui.main.account.state.AccountStateEvent
import com.gabrielpozo.openapp.ui.main.account.state.AccountStateEvent.*
import com.gabrielpozo.openapp.ui.main.account.state.AccountViewState
import com.gabrielpozo.openapp.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>() {
    override fun initViewState(): AccountViewState {
        return AccountViewState()
    }

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when (stateEvent) {
            is GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                } ?: AbsentLiveData.create()
            }

            is UpdateAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    authToken.account_pk?.let { pk ->
                        accountRepository.saveAccountProperties(
                            authToken, AccountProperties(pk, stateEvent.email, stateEvent.username)
                        )
                    }
                } ?: AbsentLiveData.create()
            }

            is ChangePasswordEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.updatePassword(
                        authToken,
                        stateEvent.currentPassword,
                        stateEvent.newPassword,
                        stateEvent.confirmNewPassword
                    )
                } ?: AbsentLiveData.create()
            }

            is None -> {
                //we set the dataState to loading = false
                return AbsentLiveData.createCancelRequest()
            }
        }
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrentNewStateOrNew()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logout() {
        sessionManager.logout()
    }


    fun cancelActiveJobs() {
        accountRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData() {
        setStateEvent(None)
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}