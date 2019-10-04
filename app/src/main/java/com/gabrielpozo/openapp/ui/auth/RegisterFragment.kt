package com.gabrielpozo.openapp.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.gabrielpozo.openapi.R
import com.gabrielpozo.openapp.util.ApiEmptyResponse
import com.gabrielpozo.openapp.util.ApiErrorResponse
import com.gabrielpozo.openapp.util.ApiSuccessResponse


class RegisterFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Gabriel", "Forgot Password Fragment ${viewModel.hashCode()}")

        viewModel.testRegister().observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is ApiSuccessResponse -> {

                    Log.d("Gabriel", "Registration Response successful ${response.body}")
                }

                is ApiErrorResponse -> {
                    Log.d("Gabriel", "Registration Response Error ${response.errorMessage}")

                }

                is ApiEmptyResponse -> {
                    Log.d("Gabriel", "Registration Response Empty")
                }
            }

        })

    }
}
