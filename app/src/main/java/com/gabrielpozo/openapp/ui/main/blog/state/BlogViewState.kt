package com.gabrielpozo.openapp.ui.main.blog.state

import com.gabrielpozo.openapp.models.BlogPost

data class BlogViewState( //BlogFragment variables
    var blogFields: BlogFields = BlogFields(),
    // ViewBlogFragment variables
    var viewBLogFields: ViewBlogFields = ViewBlogFields()

    // UpdateBLogFragment variables
) {
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false
    )


    data class ViewBlogFields(
        var blogPost: BlogPost? = null,
        var isAuthorOfTheBlogPost: Boolean = false
    )

}