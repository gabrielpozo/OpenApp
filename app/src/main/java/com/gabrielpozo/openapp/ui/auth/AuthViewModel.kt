package com.gabrielpozo.openapp.ui.auth

import androidx.lifecycle.ViewModel
import com.gabrielpozo.openapp.repository.auth.AuthRepository
import javax.inject.Inject

class AuthViewModel @Inject constructor(val authRepository: AuthRepository) : ViewModel() {
}