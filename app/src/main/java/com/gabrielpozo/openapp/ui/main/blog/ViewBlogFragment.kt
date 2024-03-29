package com.gabrielpozo.openapp.ui.main.blog

import android.os.Bundle
import android.text.format.DateUtils
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gabrielpozo.openapi.R
import com.gabrielpozo.openapp.models.BlogPost
import com.gabrielpozo.openapp.ui.DataState
import com.gabrielpozo.openapp.util.DateUtils.Companion
import com.gabrielpozo.openapp.util.DateUtils.Companion.convertLongToStringDate
import kotlinx.android.synthetic.main.fragment_view_blog.*

class ViewBlogFragment : BaseBlogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }


    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { blogViewState ->
            blogViewState.viewBLogFields.blogPost?.let { blogPost ->
                setBlogProperties(blogPost)
            }
        })
    }

    private fun setBlogProperties(blogPost: BlogPost) {
        //we set the blog properties here
        requestManager
            .load(blogPost.image)
            .into(blog_image)

        blog_title.text = blogPost.username
        blog_update_date.text = convertLongToStringDate(blogPost.date_updated)
        blog_body.setText(blogPost.body)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //TODO(check if user is author of blog post
        val isAuthorOfBlogPost = true
        if (isAuthorOfBlogPost) {
            inflater.inflate(R.menu.edit_view_menu, menu)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val isAuthorOfBlogPost = true
        if (isAuthorOfBlogPost) {
            when (item.itemId) {
                R.id.edit -> {
                    navUpdateBlogFragment()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun navUpdateBlogFragment() {
        findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
    }
}