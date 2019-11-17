package com.gabrielpozo.openapp.ui.main.blog.viewmodel

import com.gabrielpozo.openapp.models.BlogPost

/**
 * Created by Gabriel Pozo Guzman on 2019-11-15.
 */


fun BlogViewModel.setQuery(query: String) {
    val update = getCurrentNewStateOrNew()
    if (query == update.blogFields.searchQuery) {
        return
    }
    update.blogFields.searchQuery = query
    setViewState(update)
}

fun BlogViewModel.setBlogListData(blogList: List<BlogPost>) {
    val update = getCurrentNewStateOrNew()
    update.blogFields.blogList = blogList
    setViewState(update)
}

fun BlogViewModel.setBlogPost(blogPost: BlogPost) {
    val update = getCurrentNewStateOrNew()
    update.viewBLogFields.blogPost = blogPost
    setViewState(update)
}

fun BlogViewModel.setQueryExhausted(isExhausted: Boolean) {
    val update = getCurrentNewStateOrNew()
    update.blogFields.isQueryExhausted = isExhausted
    setViewState(update)
}

fun BlogViewModel.setQueryInProgress(isQueryInProgess: Boolean) {
    val update = getCurrentNewStateOrNew()
    update.blogFields.isQueryInProgress = isQueryInProgess
    setViewState(update)
}


fun BlogViewModel.setIsAuthorOfTheBlogPost(isAuthor: Boolean) {
    val update = getCurrentNewStateOrNew()
    update.viewBLogFields.isAuthorOfTheBlogPost = isAuthor
    setViewState(update)
}