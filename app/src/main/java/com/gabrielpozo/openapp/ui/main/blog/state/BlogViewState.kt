package com.gabrielpozo.openapp.ui.main.blog.state

import com.gabrielpozo.openapp.models.BlogPost

class BlogViewState( //BlogFragment variables
    var blogFields: BlogFields = BlogFields()
    // ViewBlogFragment variables

    // UpdateBLogFragment variables
) {
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = ""
    )

}