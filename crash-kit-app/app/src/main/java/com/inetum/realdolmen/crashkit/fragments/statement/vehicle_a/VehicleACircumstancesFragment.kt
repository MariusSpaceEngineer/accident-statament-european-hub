package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleACircumstancesBinding
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b.VehicleBNewStatementFragment
import com.inetum.realdolmen.crashkit.utils.printBackStack

class VehicleACircumstancesFragment : Fragment() {

    private var _binding: FragmentVehicleACircumstancesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVehicleACircumstancesBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStatementAccidentPrevious.setOnClickListener {

            requireActivity().supportFragmentManager.apply {
                popBackStack("vehicle_a_circumstances_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }

        binding.btnStatementAccidentNext.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fragmentContainerView,
                    VehicleAMiscellaneousFragment()
                )
                addToBackStack("vehicle_a_miscellaneous_fragment")
                setReorderingAllowed(true)
                commit()
            }
        }

    }
}