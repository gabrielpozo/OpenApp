package com.gabrielpozo.openapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.gabrielpozo.openapi.R
import com.gabrielpozo.openapp.ui.BaseActivity
import com.gabrielpozo.openapp.ui.ResponseType
import com.gabrielpozo.openapp.ui.main.MainActivity
import com.gabrielpozo.openapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity(), NavController.OnDestinationChangedListener {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        findNavController(R.id.auth_host_fragment).addOnDestinationChangedListener(this)

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            dataState.dataState?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let { authViewState ->
                        authViewState.authToken?.let { authToken ->
                            Log.d("Gabriel", "AuthActivity, DataState: $authToken ")
                            viewModel.setAuthToken(authToken)
                        }
                    }
                }

                data.response?.let { event ->
                    event.getContentIfNotHandled()?.let { response ->
                        when (response.responseType) {
                            is ResponseType.Dialog -> {
                                //inflate errorDialog
                            }

                            is ResponseType.Toast -> {
                                //showToast
                            }

                            is ResponseType.None -> {

                            }
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(this, Observer { viewState ->
            viewState.authToken?.let { authToken ->
                sessionManager.login(authToken)
            }

        })

        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d("Gabriel", "MainActivity: SubscribeObserver: AuthToken: $authToken")
            if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                navMainActivity()
            }
        })
    }

    private fun navMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }
}