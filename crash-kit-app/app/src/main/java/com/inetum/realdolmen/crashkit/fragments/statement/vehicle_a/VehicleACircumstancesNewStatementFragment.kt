package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleACircumstancesNewStatementBinding

class VehicleACircumstancesNewStatementFragment : Fragment() {

    private var _binding: FragmentVehicleACircumstancesNewStatementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVehicleACircumstancesNewStatementBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStatementAccidentPrevious.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack("VehicleACircumstancesFragment", 0)
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            transaction.replace(R.id.fragmentContainerView, VehicleAMiscellaneousNewStatementFragment())

            transaction.addToBackStack("VehicleAMiscellaneousFragment")
            transaction.commit()
        }

    }
}