package com.gabrielpozo.openapp.ui.main.blog

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.gabrielpozo.openapp.models.BlogPost
import com.gabrielpozo.openapp.repository.main.BlogRepository
import com.gabrielpozo.openapp.session.SessionManager
import com.gabrielpozo.openapp.ui.BaseViewModel
import com.gabrielpozo.openapp.ui.DataState
import com.gabrielpozo.openapp.ui.main.blog.state.BlogStateEvent
import com.gabrielpozo.openapp.ui.main.blog.state.BlogStateEvent.*
import com.gabrielpozo.openapp.ui.main.blog.state.BlogViewState
import com.gabrielpozo.openapp.util.AbsentLiveData
import javax.inject.Inject

class BlogViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences,
    private val requestManager: RequestManager
) : BaseViewModel<BlogStateEvent, BlogViewState>() {


    override fun initViewState(): BlogViewState {
        return BlogViewState()
    }

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        return when (stateEvent) {
            is BlogSearchEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(authToken, stateEvent.query)
                } ?: AbsentLiveData.create()
            }

            is None -> {
                AbsentLiveData.create()
            }
        }
    }


    fun setQuery(query: String) {
        val update = getCurrentNewStateOrNew()
        if (query == update.blogFields.searchQuery) {
            return
        }
        update.blogFields.searchQuery = query
        _viewState.value = update
    }

    fun setBlogListData(blogList: List<BlogPost>) {
        val update = getCurrentNewStateOrNew()
        //setting a new identical bloglist, will make not difference
        update.blogFields.blogList = blogList
        _viewState.value = update
    }

    fun cancelActiveJobs() {
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(None)
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("Gabriel", "onCleared BlogViewModel")
        cancelActiveJobs()
    }
}