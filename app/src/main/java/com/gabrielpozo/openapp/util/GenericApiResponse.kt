package com.gabrielpozo.openapp.util

import android.util.Log
import retrofit2.Response

/**
 * Copied from Architecture components google sample:
 * https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/api/ApiResponse.kt
 */
@Suppress("unused") // T is used in extending classes
sealed class GenericApiResponse<T> {

    companion object {
        private val TAG: String = "AppDebug"


        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(
                error.message ?: "unknown error"
            )
        }

        fun <T> create(response: Response<T>): GenericApiResponse<T> {

            Log.d(TAG, "GenericResponse: response: $response")
            Log.d(TAG, "GenericResponse: raw: ${response.raw()}")
            Log.d(TAG, "GenericResponse: headers: ${response.headers()}")
            Log.d(TAG, "GenericResponse: message: ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()
                return if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else if (response.code() == 401) {
                    ApiErrorResponse("401 Unauthorized. Token may be invalid.")
                } else {
                    ApiSuccessResponse(body = body)
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                return ApiErrorResponse(
                    errorMsg ?: "unknown error"
                )
            }
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : GenericApiResponse<T>()

data class ApiSuccessResponse<T>(val body: T) : GenericApiResponse<T>() {}

data class ApiErrorResponse<T>(val errorMessage: String) : GenericApiResponse<T>()
