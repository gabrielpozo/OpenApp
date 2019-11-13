package com.gabrielpozo.openapp.ui.main.blog.state

import com.gabrielpozo.openapp.models.BlogPost

class BlogViewState( //BlogFragment variables
    var blogFields: BlogFields = BlogFields(),
    // ViewBlogFragment variables
    var viewBLogFields: ViewBlogFields = ViewBlogFields()

    // UpdateBLogFragment variables
) {
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = ""
    )


    data class ViewBlogFields(
        var blogPost: BlogPost? = null,
        var isAuthorOfTheBlogPost: Boolean = false
    )

}