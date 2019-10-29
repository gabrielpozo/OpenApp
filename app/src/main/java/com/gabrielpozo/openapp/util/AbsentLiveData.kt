package com.gabrielpozo.openapp.util

import androidx.lifecycle.LiveData
import com.gabrielpozo.openapp.ui.DataState

/**
 * A LiveData class that has `null` value.
 */
class AbsentLiveData<T : Any?> private constructor() : LiveData<T>() {

    init {
        // use post instead of set since this can be created on any thread
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> {
            return AbsentLiveData()
        }

        fun <T> createCancelRequest(): LiveData<DataState<T>> {
           return object : LiveData<DataState<T>>() {
                override fun onActive() {
                    super.onActive()
                    value =  DataState.loading(loading = false)
                }
            }
        }
    }
}
