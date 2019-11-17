package com.gabrielpozo.openapp.ui.main.blog.viewmodel

/**
 * Created by Gabriel Pozo Guzman on 2019-11-15.
 */

fun BlogViewModel.getPage(): Int {
    getCurrentNewStateOrNew().let {
        return it.blogFields.page
    }
}

fun BlogViewModel.getQueryExhausted(): Boolean {
    getCurrentNewStateOrNew().let {
        return it.blogFields.isQueryExhausted
    }
}

fun BlogViewModel.getIsQueryInProgress(): Boolean {
    getCurrentNewStateOrNew().let {
        return it.blogFields.isQueryInProgress
    }
}

fun BlogViewModel.getSearchQuery(): String {
    getCurrentNewStateOrNew().let {
        return it.blogFields.searchQuery
    }
}