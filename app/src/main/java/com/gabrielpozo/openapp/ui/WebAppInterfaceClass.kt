package com.gabrielpozo.openapp.ui

import android.webkit.JavascriptInterface

class WebAppInterfaceClass constructor(private val calllback: OnWebInteractionCallback) {
    private val TAG: String = "Gabriel"
    @JavascriptInterface
    fun onSuccess(email: String) {
        calllback.onSuccess(email)
    }

    @JavascriptInterface
    fun onError(errorMessage: String) {
        calllback.onError(errorMessage)
    }

    @JavascriptInterface
    fun onLoading(isLoading: Boolean) {
        calllback.onLoading(isLoading)
    }

    interface OnWebInteractionCallback {
        fun onSuccess(email: String)
        fun onError(errorMessage: String)
        fun onLoading(isLoading: Boolean)
    }
}
