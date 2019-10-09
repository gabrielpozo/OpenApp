package com.gabrielpozo.openapp.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.gabrielpozo.openapi.R
import com.gabrielpozo.openapp.ui.*
import com.gabrielpozo.openapp.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ClassCastException

class ForgotPasswordFragment : BaseAuthFragment(), WebAppInterfaceClass.OnWebInteractionCallback {

    lateinit var webView: WebView
    lateinit var stateChangeListener: DataStateChangeListener

    private val TAG: String = "Gabriel"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Gabriel", "Forgot Password Fragment ${viewModel.hashCode()}")
        webView = view.findViewById(R.id.webview)

        loadPasswordResetWebView()
        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadPasswordResetWebView() {
        stateChangeListener.onDataStateChange(DataState.loading(true, null))

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                stateChangeListener.onDataStateChange(DataState.loading(false, null))
            }
        }
        webView.loadUrl(Constants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebAppInterfaceClass(this), "AndroidTextListener")
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener.")
        }
    }

    override fun onSuccess(email: String) {
        Log.d(TAG, "onSuccess: a reset link will be sent to $email")
        onPasswordResetLinkSent()

    }

    override fun onError(errorMessage: String) {
        val dataState = DataState.error<Any>(response = Response(errorMessage, ResponseType.Dialog))
        stateChangeListener.onDataStateChange(dataState)
    }

    override fun onLoading(isLoading: Boolean) {
        Log.d(TAG, "onLoading")
        GlobalScope.launch(Main) {
            stateChangeListener.onDataStateChange(DataState.loading<Any>(loading = isLoading))

        }
    }

    private fun onPasswordResetLinkSent() {
        GlobalScope.launch(Main) {
            parent_view.removeView(webView)
            webView.destroy()
            val animation = TranslateAnimation(
                password_reset_done_container.width.toFloat()
                , 0f, 0f, 0f
            )
            animation.duration = 500
            password_reset_done_container.startAnimation(animation)
            password_reset_done_container.visibility = View.VISIBLE
        }


    }

}
