package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleANewStatementBinding
import com.inetum.realdolmen.crashkit.utils.printBackStack

class VehicleANewStatementFragment : Fragment() {
    private var _binding: FragmentVehicleANewStatementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVehicleANewStatementBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        binding.btnStatementAccidentPrevious.setOnClickListener {
            findNavController().apply {
                navigate(R.id.newStatementFragment)
            }
        }

        binding.btnStatementAccidentNext.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainerView, VehicleAInsuranceFragment())
                addToBackStack("vehicle_a_insurance_fragment")
                setReorderingAllowed(true)
                commit()
            }
        }

    }

}