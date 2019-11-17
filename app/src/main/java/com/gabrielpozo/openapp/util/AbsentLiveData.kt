package com.gabrielpozo.openapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.gabrielpozo.openapp.ui.DataState
import com.gabrielpozo.openapp.ui.Loading

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
            return liveData {
                emit(DataState(null, Loading(false), null))
            }
        }
    }
}
