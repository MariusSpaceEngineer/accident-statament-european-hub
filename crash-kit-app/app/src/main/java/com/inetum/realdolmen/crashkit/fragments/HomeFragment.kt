package com.inetum.realdolmen.crashkit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.inetum.realdolmen.crashkit.CrashKitApp
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentHomeBinding
import com.inetum.realdolmen.crashkit.helpers.FragmentNavigationHelper

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val fragmentNavigationHelper by lazy {
        FragmentNavigationHelper(requireActivity().supportFragmentManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (CrashKitApp.securedPreferences.isGuest()) {
            binding.cvHomeQr.visibility = View.GONE
        }

        binding.cvHomeStatement.setOnClickListener {
            findNavController().navigate(R.id.newStatementFragment)
        }

        binding.cvHomeQr.setOnClickListener {
            fragmentNavigationHelper.navigateToFragment(
                R.id.fragmentContainerView,
                ShareInsuranceInformationFragment(),
                "share_insurance_fragment"
            )
        }
    }
}