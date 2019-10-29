package com.gabrielpozo.openapp.ui.main.blog.state

sealed class BlogStateEvent {
    data class BlogSearchEvent(val query: String) : BlogStateEvent()
    object None : BlogStateEvent()
}