package com.inetum.realdolmen.crashkit.fragments.statement.vehicle_a

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inetum.realdolmen.crashkit.NewStatementViewModel
import com.inetum.realdolmen.crashkit.R
import com.inetum.realdolmen.crashkit.databinding.FragmentVehicleAMiscellaneousBinding
import com.inetum.realdolmen.crashkit.fragments.statement.vehicle_b.VehicleBNewStatementFragment
import com.inetum.realdolmen.crashkit.utils.printBackStack


class VehicleAMiscellaneousFragment : Fragment() {

    private var _binding: FragmentVehicleAMiscellaneousBinding? = null
    private val binding get() = _binding!!
    private lateinit var model: NewStatementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity())[NewStatementViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =
            FragmentVehicleAMiscellaneousBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.printBackStack()

        // Observe the statementData
        model.statementData.observe(viewLifecycleOwner, Observer { statementData ->
            // Update the UI here based on the new statementData
            binding.etStatementVehicleADriverRemarks.setText(statementData.vehicleARemarks)
            binding.etStatementVehicleADamageDescription.setText(statementData.vehicleADamageDescription)
        })

        binding.btnStatementAccidentPrevious.setOnClickListener {

            requireActivity().supportFragmentManager.apply {
                popBackStack(
                    "vehicle_a_miscellaneous_fragment",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

            }
        }

        binding.btnStatementAccidentNext.setOnClickListener {

            model.statementData.value?.apply {
                this.vehicleARemarks = binding.etStatementVehicleADriverRemarks.text.toString()
                this.vehicleADamageDescription =
                    binding.etStatementVehicleADamageDescription.text.toString()
            }

            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fragmentContainerView,
                    VehicleBNewStatementFragment()
                )
                addToBackStack("vehicle_b_new_statement_fragment")
                setReorderingAllowed(true)
                commit()
            }
        }
    }
}
