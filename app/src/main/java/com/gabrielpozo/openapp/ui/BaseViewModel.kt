package com.gabrielpozo.openapp.ui

import androidx.lifecycle.*

abstract class BaseViewModel<StateEvent, ViewState> : ViewModel() {

    protected val _stateEvent: MutableLiveData<StateEvent> = MutableLiveData()
    protected val _viewState: MutableLiveData<ViewState> = MutableLiveData()

    val viewState: LiveData<ViewState>
        get() = _viewState



    val dataState: LiveData<DataState<ViewState>> = _stateEvent.switchMap { stateEvent ->
        handleStateEvent(stateEvent)
    }


    fun setStateEvent(event: StateEvent) {
        _stateEvent.value = event
    }

    fun getCurrentNewStateOrNew(): ViewState {
        return viewState.value?.let {
            it
        } ?: initViewState()
    }

    abstract fun initViewState(): ViewState

    abstract fun handleStateEvent(stateEvent: StateEvent): LiveData<DataState<ViewState>>

}