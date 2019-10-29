package com.gabrielpozo.openapp.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
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
        dataState?.let { dataState ->
            GlobalScope.launch(Dispatchers.Main) {
                displayProgressBar(dataState.loading.isLoading)

                dataState.error?.let { errorEvent ->
                    handleStateError(errorEvent)
                }

                dataState.data?.let { data ->
                    data.response?.let { responseEvent ->
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
        event.getContentIfNotHandled()?.let { response ->
            when (response.responseType) {
                is ResponseType.Toast -> {
                    response.message?.let { message ->
                        displayToast(message)
                    }
                }

                is ResponseType.Dialog -> {
                    response.message?.let { message ->
                        displaySuccessDialog(message)
                    }
                }

                is ResponseType.None -> {
                    Log.e(TAG, "handleStateResponse: ${response.message}")
                }
            }
        }
    }

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)

        }
    }

    abstract fun displayProgressBar(bool: Boolean)
}