package com.gabrielpozo.openapp.ui

import android.util.Log
import com.gabrielpozo.openapp.session.SessionManager
import com.gabrielpozo.openapp.util.extensions.displayErrorDialog
import com.gabrielpozo.openapp.util.extensions.displaySuccessDialog
import com.gabrielpozo.openapp.util.extensions.displayToast
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), DataStateChangeListener {
    private val TAG: String = "Gabriel"

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            GlobalScope.launch(Dispatchers.Main) {
                displayProgressBar(it.loading.isLoading)
                it.error?.let { errorEvent ->
                    handleStateError(errorEvent)
                }
                it.dataState?.let {
                    it.response?.let { responseEvent ->
                        handleStateResponse(responseEvent)
                    }
                }
            }
        }
    }

    private fun handleStateError(errorEvent: Event<StateError>) {
        errorEvent.getContentIfNotHandled()?.let {
            when (it.response.responseType) {
                is ResponseType.Toast -> {
                    it.response.message?.let { message ->
                        displayToast(message)
                        displayProgressBar(false)
                    }
                }

                is ResponseType.Dialog -> {
                    it.response.message?.let { message ->
                        displayErrorDialog(message)
                        displayProgressBar(false)

                    }
                }

                is ResponseType.None -> {
                    Log.e(TAG, "handleStateError: ${it.response.message}")
                }
            }
            displayProgressBar(false)
        }
    }

    private fun handleStateResponse(event: Event<Response>) {
        event.getContentIfNotHandled()?.let {
            when (it.responseType) {
                is ResponseType.Toast -> {
                    it.message?.let {
                        displayToast(it)
                    }
                }

                is ResponseType.Dialog -> {
                    it.message?.let { message ->
                        displaySuccessDialog(message)
                    }

                }

                is ResponseType.None -> {
                    Log.e(TAG, "handleStateResponse: ${it.message}")
                }
            }
        }

    }


    abstract fun displayProgressBar(bool: Boolean)
}