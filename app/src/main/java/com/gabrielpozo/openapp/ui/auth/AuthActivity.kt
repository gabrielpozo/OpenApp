package com.gabrielpozo.openapp.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gabrielpozo.openapi.R
import com.gabrielpozo.openapp.ui.BaseActivity

class AuthActivity : BaseActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}