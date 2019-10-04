package com.gabrielpozo.openapp.ui.auth.state

import com.gabrielpozo.openapp.models.AuthToken


data class AuthViewState(
    var registrationFields: RegistrationFields? = RegistrationFields(),
    var loginFields: LoginFields? = LoginFields(),
    var authToken: AuthToken? = null
)

data class RegistrationFields(
    val registration_email: String? = null,
    val registration_username: String? = null,
    val registration_password: String? = null,
    val registration_confirm_password: String? = null
) {
    class RegistrationError {
        companion object {
            fun mustFillAllFields() = "All fields are required."

            fun passwordsDoNotMatch() = "Passwords must match."

            fun none() = "None."
        }
    }

    fun isValidForRegistration(): String {
        if (registration_email.isNullOrEmpty() ||
            registration_username.isNullOrEmpty() ||
            registration_password.isNullOrEmpty() ||
            registration_confirm_password.isNullOrEmpty()
        ) {
            return RegistrationError.mustFillAllFields()
        }

        if (registration_password != registration_confirm_password) {
            return RegistrationError.passwordsDoNotMatch()
        }

        return RegistrationError.none()
    }

}

data class LoginFields(
    var login_email: String? = null,
    var login_password: String? = null
) {
    class LoginError {

        companion object {

            fun mustFillAllFields(): String {
                return "You can't login without an email and password."
            }

            fun none(): String {
                return "None"
            }

        }
    }

    fun isValidForLogin(): String {

        if (login_email.isNullOrEmpty()
            || login_password.isNullOrEmpty()
        ) {

            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }

    override fun toString(): String {
        return "LoginState(email=$login_email, password=$login_password)"
    }
}
