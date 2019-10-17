package com.gabrielpozo.openapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.gabrielpozo.openapi.R
import com.gabrielpozo.openapp.ui.BaseActivity
import com.gabrielpozo.openapp.ui.auth.state.AuthStateEvent
import com.gabrielpozo.openapp.ui.main.MainActivity
import com.gabrielpozo.openapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import com.gabrielpozo.openapp.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : BaseActivity(), NavController.OnDestinationChangedListener {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    private val TAG: String = "Gabriel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        findNavController(R.id.auth_host_fragment).addOnDestinationChangedListener(this)

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            onDataStateChange(dataState)

            dataState.dataState?.let { data ->
                data.response?.let { event ->
                    event.peekContent().let { response ->
                        response.message?.let { message ->
                            if (message.equals(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)) {
                                onFinishCheckPreviousAuthUser()
                            }
                        }
                    }
                }
            }


            dataState.dataState?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let { authViewState ->
                        authViewState.authToken?.let { authToken ->
                            Log.d(TAG, "AuthActivity, DataState: $authToken ")
                            viewModel.setAuthToken(authToken) // we do this action in order to login
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
            Log.d(TAG, "MainActivity: SubscribeObserver: AuthToken: $authToken")
            if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                navMainActivity()
            }
        })
    }

    private fun checkPreviousAuthUser() {
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent)
    }

    private fun onFinishCheckPreviousAuthUser() {
        fragment_container.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()
        checkPreviousAuthUser()
    }


    override fun displayProgressBar(bool: Boolean) {
        if (bool) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.INVISIBLE
        }
    }

}