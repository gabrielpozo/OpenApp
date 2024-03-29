package com.gabrielpozo.openapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gabrielpozo.openapi.R
import com.gabrielpozo.openapp.models.AccountProperties
import com.gabrielpozo.openapp.ui.main.account.state.AccountStateEvent
import kotlinx.android.synthetic.main.fragment_account.*


class AccountFragment : BaseAccountFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        change_password.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        logout_button.setOnClickListener {
            viewModel.logout()
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.let {
                it.data?.let { data ->
                    data.data?.let { event ->
                        event.getContentIfNotHandled()?.let { accountViewState ->
                            accountViewState.accountProperties?.let { accountProperties ->
                                Log.d(
                                    TAG,
                                    "AccountFragment, DataState $accountProperties"
                                )
                                viewModel.setAccountPropertiesData(accountProperties)
                            }
                        }
                    }
                }
            }

            viewModel.viewState.observe(viewLifecycleOwner, Observer { accountViewState ->
                accountViewState?.let {
                    accountViewState.accountProperties?.let { accountProperties ->
                        Log.d(TAG, "AccountFragment, ViewSate: $accountProperties")
                        setAccountDataFields(accountProperties)
                    }
                }
            })
        })
    }

    private fun setAccountDataFields(accountProperties: AccountProperties) {
        email?.text = accountProperties.email
        username?.text = accountProperties.userName
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> {
                findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.setStateEvent(AccountStateEvent.GetAccountPropertiesEvent)
    }
}