package com.gabrielpozo.openapp.ui

interface DataChangeStateListener {
    fun onDataStateChange(data: DataState<*>?)
}