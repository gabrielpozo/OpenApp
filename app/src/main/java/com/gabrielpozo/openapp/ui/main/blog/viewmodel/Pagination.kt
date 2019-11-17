package com.gabrielpozo.openapp.ui.main.blog.viewmodel

import android.util.Log
import com.gabrielpozo.openapp.ui.main.blog.state.BlogStateEvent.*
import com.gabrielpozo.openapp.ui.main.blog.state.BlogViewState

/**
 * Created by Gabriel Pozo Guzman on 2019-11-15.
 */
private val TAG: String = "Gabriel"

fun BlogViewModel.resetPage() {
    val update = getCurrentNewStateOrNew()
    update.blogFields.page = 1
    setViewState(update)
}

fun BlogViewModel.executeSearch() {
    setQueryInProgress(true)
    setQueryExhausted(false)
    //  resetPage()
    setStateEvent(BlogSearchEvent())
}

fun BlogViewModel.incrementPageNumber() {
    val update = getCurrentNewStateOrNew()
    val page = update.copy().blogFields.page
    /**CHECK THIS OUT**/
    update.blogFields.page = page + 1
}


fun BlogViewModel.nextPage() {
    if (!getQueryExhausted() && !getIsQueryInProgress()) {
        Log.d(TAG, "attempting to load the next page")
        incrementPageNumber()
        setQueryInProgress(true)
        setStateEvent(BlogSearchEvent())
    }
}

fun BlogViewModel.handleIncomingBlogListData(viewState: BlogViewState) {
    setQueryExhausted(viewState.blogFields.isQueryExhausted)
    setQueryInProgress(viewState.blogFields.isQueryInProgress)
    setBlogListData(viewState.blogFields.blogList)
}