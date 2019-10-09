package com.gabrielpozo.openapp.ui

interface DataStateChangeListener {
    fun onDataStateChange(data: DataState<*>?)
}