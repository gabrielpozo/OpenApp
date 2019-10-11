package com.gabrielpozo.openapp.ui.main.account

import android.content.Context
import android.util.Log
import com.gabrielpozo.openapp.ui.DataStateChangeListener
import dagger.android.support.DaggerFragment

abstract class BaseAccountFragment : DaggerFragment(){

    val TAG: String = "Gabriel"

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
    }
}