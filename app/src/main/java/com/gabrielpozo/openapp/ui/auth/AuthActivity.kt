package com.gabrielpozo.openapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gabrielpozo.openapi.R
import com.gabrielpozo.openapp.ui.BaseActivity
import com.gabrielpozo.openapp.ui.main.MainActivity
import com.gabrielpozo.openapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)

        subscribeObservers()
    }

    private fun subscribeObservers() {
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
}