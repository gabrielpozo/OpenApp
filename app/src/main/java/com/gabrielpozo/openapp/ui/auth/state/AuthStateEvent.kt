package com.gabrielpozo.openapp.ui.auth.state

sealed class AuthStateEvent {

    data class LoginAttemptStateEvent(val email: String, val password: String) : AuthStateEvent()
    data class RegisterAttemptStateEvent(
        val email: String,
        val username: String,
        val password: String,
        val confirm_password: String
    ) : AuthStateEvent()

    object CheckPreviousAuthEvent : AuthStateEvent()

    object None : AuthStateEvent()
}