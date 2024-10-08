package com.inetum.realdolmen.crashkit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inetum.realdolmen.crashkit.R

class LoadingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    fun showLoadingFragment() {
        view?.isClickable= true
        view?.isFocusable= true
        view?.visibility = View.VISIBLE
    }

    fun hideLoadingFragment() {
        view?.isClickable= false
        view?.isFocusable= false
        view?.visibility = View.GONE
    }
}