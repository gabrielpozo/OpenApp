package com.gabrielpozo.openapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.gabrielpozo.openapp.ui.DataState
import com.gabrielpozo.openapp.ui.Response
import com.gabrielpozo.openapp.ui.ResponseType
import com.gabrielpozo.openapp.util.*
import com.gabrielpozo.openapp.util.Constants.Companion.NETWORK_TIMEOUT
import com.gabrielpozo.openapp.util.Constants.Companion.TESTING_CACHE_DELAY
import com.gabrielpozo.openapp.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.gabrielpozo.openapp.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.gabrielpozo.openapp.util.ErrorHandling.Companion.ERROR_EMPTY_RESPONSE
import com.gabrielpozo.openapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.gabrielpozo.openapp.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.gabrielpozo.openapp.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>(
    isNetworkAvailable: Boolean, //is there a network connection
    isNetworkRequest: Boolean, // is there an network request
    shouldLoadFromCache: Boolean = false,// should the cached data should de loaded
    shouldCancelIfnotInternet: Boolean = false
) {

    private val TAG: String = "Gabriel"

    val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        coroutineScope.launch {
            if (shouldFetch()) {
                withContext(Main) {
                    setValue(DataState.loading(loading = true))
                }
                if (shouldLoadFromCache) {
                    val dbSource = loadFromCache()
                    withContext(Main) {
                        result.addSource(dbSource) { viewStateType ->
                            result.removeSource(dbSource)
                            setValue(DataState.loading(loading = true, cachedData = viewStateType))
                        }
                    }
                }

                if (isNetworkRequest && isNetworkAvailable) {
                    doNetWorkRequest()
                } else {
                    if (shouldCancelIfnotInternet) {
                        onErrorReturn(
                            UNABLE_TODO_OPERATION_WO_INTERNET,
                            shouldUseDialog = true,
                            shouldUseToast = false
                        )
                    } else {
                        doCacheRequest()
                    }
                }
            }
        }
    }

    private suspend fun doCacheRequest() {
        //fake delay for testing cache
        delay(TESTING_CACHE_DELAY)
        //View data from cache ONLY and return
        createCacheRequestAndReturn()
    }

    private suspend fun doNetWorkRequest() {
        //simulate a Network delay
        delay(TESTING_NETWORK_DELAY)
        withContext(Main) {
            val apiResponse = createCall()
            result.addSource(apiResponse) { response ->
                result.removeSource(apiResponse)
                coroutineScope.launch {
                    handleNetworkResponse(response)
                }
            }
        }

        GlobalScope.launch {
            delay(NETWORK_TIMEOUT)
            if (!job.isCompleted) {
                job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
            }
        }
    }

    private suspend fun handleNetworkResponse(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }

            is ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, shouldUseDialog = true, shouldUseToast = false)
            }

            is ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: request return nothing (HTTP 204")
                onErrorReturn(
                    ERROR_EMPTY_RESPONSE,
                    shouldUseDialog = true,
                    shouldUseToast = false
                )
            }
        }
    }


    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onErrorReturn(
        errorMessage: String? = null,
        shouldUseDialog: Boolean,
        shouldUseToast: Boolean
    ) {
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None

        if (msg == null) {
            msg = ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(msg)) {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast
        }
        if (useDialog) {
            responseType = ResponseType.Dialog
        }
        onCompleteJob(
            DataState.error(
                response = Response(
                    message = msg,
                    responseType = responseType
                )
            )
        )
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        job = Job()
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        cause?.let {
                            onErrorReturn(
                                it.message,
                                shouldUseDialog = false,
                                shouldUseToast = true

                            )
                        } ?: onErrorReturn(
                            ERROR_UNKNOWN,
                            shouldUseDialog = false,
                            shouldUseToast = true
                        )

                    } else if (job.isCompleted) {
                        Log.e(TAG, "NetworkBoundResource: Job has been completed ")
                        //Do nothing. Should be handled now already
                    }
                }
            })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract suspend fun createCacheRequestAndReturn()

    abstract fun loadFromCache(): LiveData<ViewStateType>

    abstract suspend fun updateLocalDatabase(cacheObject: CacheObject?)

    abstract fun setJob(job: Job)

    open fun shouldFetch() = true

}

