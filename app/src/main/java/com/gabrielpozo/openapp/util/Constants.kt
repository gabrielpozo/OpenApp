package com.gabrielpozo.openapp.util

class Constants {

    companion object {

        const val accountproperties_delay_refresh = 8// every 8 seconds
        const val accountproperties_refresh_time = accountproperties_delay_refresh * 1
        const val accountproperties_update_immediately = accountproperties_delay_refresh * 2
        const val BASE_URL = "https://open-api.xyz/api/"
        const val PASSWORD_RESET_URL = "https://open-api.xyz/password_reset/"
        const val NETWORK_TIMEOUT = 12000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing
    }
}