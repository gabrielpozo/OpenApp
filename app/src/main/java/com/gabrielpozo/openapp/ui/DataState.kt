package com.gabrielpozo.openapp.ui

data class DataState<T>(
    val error: Event<StateError>? = null,
    val loading: Loading = Loading(false),
    val dataState: Data<T>? = null
) {
    companion object {
        fun <T> error(response: Response): DataState<T> {
            return DataState(error = Event(StateError(response)))
        }

        fun <T> loading(loading: Boolean, cachedData: T? = null): DataState<T> {
            return DataState(
                loading = Loading(loading),
                dataState = Data(Event.dataEvent(cachedData), null)
            )
        }

        fun <T> dataState(data: T?, response: Response): DataState<T> {
            return DataState(
                dataState = Data(
                    data = Event.dataEvent(data),
                    response = Event.dataEvent(response)
                )
            )
        }
    }
}
