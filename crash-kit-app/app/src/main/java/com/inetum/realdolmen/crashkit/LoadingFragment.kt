package com.inetum.realdolmen.crashkit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class LoadingFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    fun showLoadingFragment() {
        view?.visibility = View.VISIBLE
    }

    fun hideLoadingFragment() {
        view?.visibility = View.GONE
    }
}