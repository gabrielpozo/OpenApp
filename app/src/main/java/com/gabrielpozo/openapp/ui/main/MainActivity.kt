package com.gabrielpozo.openapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.gabrielpozo.openapi.R
import com.gabrielpozo.openapp.ui.BaseActivity
import com.gabrielpozo.openapp.ui.auth.AuthActivity
import com.gabrielpozo.openapp.ui.main.account.BaseAccountFragment
import com.gabrielpozo.openapp.ui.main.account.ChangePasswordFragment
import com.gabrielpozo.openapp.ui.main.account.UpdateAccountFragment
import com.gabrielpozo.openapp.ui.main.blog.BaseBlogFragment
import com.gabrielpozo.openapp.ui.main.blog.UpdateBlogFragment
import com.gabrielpozo.openapp.ui.main.blog.ViewBlogFragment
import com.gabrielpozo.openapp.ui.main.create_blog.BaseCreateBlogFragment
import com.gabrielpozo.openapp.util.BottomNavController
import com.gabrielpozo.openapp.util.setUpNavigation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.progress_bar

class MainActivity : BaseActivity(), BottomNavController.NavGraphProvider,
    BottomNavController.OnNavigationGraphChanged,
    BottomNavController.OnNavigationReselectedListener {

    private val TAG: String = "Gabriel"
    private lateinit var bottomNavigationView: BottomNavigationView
    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this,
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tool_bar.setOnClickListener {
            sessionManager.logout()
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }
        setupActionBar()
        subscribeObservers()
    }

    private fun setupActionBar() {
        setSupportActionBar(tool_bar)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "MainActivity: SubscribeObserver: AuthToken: $authToken")
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
                navAuthActivity()
            }
        })
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(bool: Boolean) {
        if (bool) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

    override fun getNavGraphId(itemId: Int) = when (itemId) {
        R.id.nav_blog -> {
            R.navigation.nav_blog
        }

        R.id.nav_account -> {
            R.navigation.nav_account
        }

        R.id.nav_create_blog -> {
            R.navigation.nav_create_blog
        }

        else -> {
            R.navigation.nav_blog
        }
    }

    override fun onGraphChanged() {
        expandBar()
        cancelActiveJobs()
    }

    private fun cancelActiveJobs() {
        bottomNavController.fragmentManager.findFragmentById(bottomNavController.containerId)
            ?.childFragmentManager
            ?.fragments?.forEach { fragment ->
            when (fragment) {
                is BaseAccountFragment -> fragment.cancelActiveJobs()
                is BaseBlogFragment -> fragment.cancelActiveJobs()
                is BaseCreateBlogFragment -> fragment.cancelActiveJobs()
            }
        }
        displayProgressBar(false)
    }

    override fun onReselectNavItem(navController: NavController, fragment: Fragment) =
        when (fragment) {
            is ViewBlogFragment -> {
                navController.navigate(R.id.action_viewBlogFragment_to_home)
            }
            is UpdateBlogFragment -> {
                navController.navigate(R.id.action_updateBlogFragment_to_home)
            }
            is UpdateAccountFragment -> {
                navController.navigate(R.id.action_updateAccountFragment_to_home)
            }
            is ChangePasswordFragment -> {
                navController.navigate(R.id.action_changePasswordFragment_to_home)
            }
            else -> {
                //do nothing
            }

        }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    private fun expandBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }
}