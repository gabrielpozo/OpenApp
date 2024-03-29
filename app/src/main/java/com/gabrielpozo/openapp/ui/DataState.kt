package com.gabrielpozo.openapp.ui

data class DataState<T>(
    val error: Event<StateError>? = null,
    val loading: Loading = Loading(false),
    val data: Data<T>? = null
) {
    companion object {
        fun <T> error(response: Response): DataState<T> {
            return DataState(error = Event(StateError(response)))
        }

        fun <T> loading(loading: Boolean, cachedData: T? = null): DataState<T> {
            return DataState(
                loading = Loading(loading),
                data = Data(Event.dataEvent(cachedData), null)
            )
        }

        fun <T> data(data: T?, response: Response? = null): DataState<T> {
            return DataState(
                data = Data(
                    data = Event.dataEvent(data),
                    response = Event.dataEvent(response)
                )
            )
        }
    }
}
