package com.gabrielpozo.openapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.gabrielpozo.openapp.api.main.OpenMainService
import com.gabrielpozo.openapp.api.main.responses.BlogListSearchResponse
import com.gabrielpozo.openapp.models.AuthToken
import com.gabrielpozo.openapp.models.BlogPost
import com.gabrielpozo.openapp.persistence.BlogPostDao
import com.gabrielpozo.openapp.repository.JobManager
import com.gabrielpozo.openapp.repository.NetworkBoundResource
import com.gabrielpozo.openapp.session.SessionManager
import com.gabrielpozo.openapp.ui.DataState
import com.gabrielpozo.openapp.ui.main.blog.state.BlogViewState
import com.gabrielpozo.openapp.util.ApiSuccessResponse
import com.gabrielpozo.openapp.util.DateUtils
import com.gabrielpozo.openapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class BlogRepository @Inject constructor(
    val openMainService: OpenMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("BlogRepository") {
    private val TAG: String = "Gabriel"

    fun searchBlogPosts(authToken: AuthToken, query: String): LiveData<DataState<BlogViewState>> {
        return object :
            NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                false,
                true
            ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {
                val blogPostList: ArrayList<BlogPost> = ArrayList()
                for (blogPostResponse in response.body.results) {
                    blogPostList.add(
                        BlogPost(
                            pk = blogPostResponse.pk,
                            title = blogPostResponse.title,
                            slug = blogPostResponse.slug,
                            body = blogPostResponse.body,
                            image = blogPostResponse.image,
                            date_updated = DateUtils.convertServerStringDateToLong(
                                blogPostResponse.date_updated
                            ),
                            username = blogPostResponse.username
                        )
                    )
                }

                updateLocalDatabase(blogPostList)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return openMainService.searchListBlogPosts("Token ${authToken.token!!}", query)
            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    //finish by viewing the db cache
                    result.addSource(loadFromCache()) { viewState ->
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }

            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDao.getAllBLogPosts().switchMap {blogPosts ->
                    liveData {
                        emit(BlogViewState(BlogViewState.BlogFields(blogList = blogPosts)))
                    }
                }
            }

            override suspend fun updateLocalDatabase(cacheObject: List<BlogPost>?) {
                cacheObject?.let { blogPosts ->
                    withContext(IO) {
                        blogPosts.forEach { blogPost ->
                            try {
                                //launch each insert as a separate job to executed in parallel
                                launch {
                                    Log.d(TAG, "updateLocalDataBase: inserting Blog: $blogPost")
                                    blogPostDao.insert(blogPost)
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    TAG, "updateLocalDb: error updating cache " +
                                            "on blogPosts with slug ${blogPost.slug}"
                                )
                                //optional error handling??
                            }
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }

        }.asLiveData()

    }

}